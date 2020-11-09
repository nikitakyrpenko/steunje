package com.negeso.module.inquiry;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.module.inquiry.domain.Option;
import com.negeso.module.inquiry.domain.Question;
import com.negeso.module.inquiry.domain.QuestionType;
import com.negeso.module.inquiry.domain.Questionnaire;

public class RespondentController {
    
    private final Element model;
    private Element elQuiz;
    private final Questionnaire quiz;
    private final RequestContext req;
    private Inquiry inquiry = Inquiry.getInstance();
    private static Logger logger = Logger.getLogger(RespondentController.class);
    
    RespondentController(Element model, Questionnaire quiz, RequestContext req) {
        this.model = model;
        this.quiz = quiz;
        this.req = req;
    }
    
    public boolean execute() {
        logger.debug("+ -");
        return quiz.isMultipage() ? executeMultipage() : executeSinglepage();
    }

    /** Returns true when is the quiz is finished and can be persisted */
    private boolean executeMultipage() {
        logger.debug("+");
        elQuiz = XHelper.addXmlQuestionnaire(model, quiz);
        List<Question> questions =
            inquiry.getQuestionsByQuestionnaireId(quiz.getId());
        elQuiz.setAttribute("total_questions", "" + questions.size());
        if (questions.size() == 0) {
            logger.debug("- quiz is empty");
            return false;
        }
        if (req.getParameter("inquiry_question_id") == null) {
            addQuestionWithAnswerToModel(questions.get(0), null);
            logger.debug("- start the quiz");
            return false;
        }
        if (req.getParameter("inquiry_previous_question") != null) {
            addQuestionWithAnswerToModel(getPreviousQuestion(questions), null);
            logger.debug("- proceed to previous question");
            return false;
        }
        int qId = Integer.parseInt(req.getParameter("inquiry_question_id"));
        Question currQ = inquiry.loadQuestion(qId);
        QuestionAnswer qa = createQuestionAnswerFromRequest(currQ);
        if (req.getParameter("preview")!=null) {
            addQuestionWithAnswerToModel(currQ, qa);
            return false;
        }
        if (!qa.isComplete()) {
        	addQuestionWithAnswerToModel(currQ, qa);
        	warnAboutMandatoryQuestion();
        	logger.debug("- the question is obligatory");
        	return false;
        }
        sessionSaveAnswer(qa);
        if (getNextQuestion(questions) != null) {
            addQuestionWithAnswerToModel(getNextQuestion(questions), null);
            logger.debug("- proceed to next question");
            return false;
        }
        for (Question q : questions) {
            if( !sessionGetAnswer(q.getId()).isComplete() ) {
                addQuestionWithAnswerToModel(q, null);
                warnAboutMandatoryQuestion();
                logger.warn("- qustion " + q.getId() + " not answered");
                return false;
            }
        }
        for (Question q : questions) addQuestionWithAnswerToModel(q, null);
        Xbuilder.setAttr(model, "status", "complete");
        logger.debug("- quiz is finished");
        return true;
    }

    private void warnAboutMandatoryQuestion() {
        Xbuilder.setAttr(model, "status", "incomplete");
    }
    
    private boolean executeSinglepage() {
        logger.debug("+");
        elQuiz = XHelper.addXmlQuestionnaire(model, quiz);
        List<Question> questions =
            inquiry.getQuestionsByQuestionnaireId(quiz.getId());
        if (questions.size() == 0) {
            logger.debug("- quiz is empty");
            return false;
        }
        boolean complete = true;
        for (Question q : questions) {
            QuestionAnswer qa = createQuestionAnswerFromRequest(q);
            if (!qa.isComplete()) complete = false;
            addQuestionWithAnswerToModel(q, qa);
        }
        if (req.getParameter("inquiry_submit") == null) {
            logger.debug("- start the quiz");
            return false;
        }
        if (!complete) {
            warnAboutMandatoryQuestion();
            logger.debug("- the questionnaire is incomplete");
            return false;
        }
        Xbuilder.setAttr(model, "status", "complete");
        logger.debug("- quiz is finished");
        return true;
    }

    /** Creates a serializable QuestionAnswer based on request parameters */
    private QuestionAnswer createQuestionAnswerFromRequest(Question q) {
        logger.debug("+");
        int qId = q.getId();
        QuestionAnswer qa = new QuestionAnswer(qId);
        qa.setAltAnswer(req.getParameter("inquiry_alternative_" + qId));
        qa.setRemark(req.getParameter("inquiry_remark_" + qId));
        if(q.getType() == QuestionType.TEXT || q.getType() == QuestionType.TEXTAREA) {
            qa.setTextAnswer(req.getString("inquiry_question_" + qId, ""));
        } else {
            List ids = Collections.EMPTY_LIST;
            String[] ans = req.getParameters("inquiry_question_" + qId);
            if(ans != null) ids = Arrays.asList(ans);
            List<Option> opts = inquiry.getOptionsByQuestionId(qId);
            for (Option o : opts) {
                qa.setOptionValue(o.getId(), ids.contains("" + o.getId()));
            }
        }
        logger.debug("-");
        return qa;
    }

    /**
     * Adds a question and the answer to it to <b>model</b>.
     * @param   q       question being added
     * @param   qa      QuestionAnswer object. If null, it is searched in
     *                  session. If neither set, nor present in session,
     *                  empty answer added.
     */
    private void addQuestionWithAnswerToModel(Question q, QuestionAnswer qa) {
        logger.debug("+");
        if (qa == null) qa = sessionGetAnswer(q.getId());
        if (qa == null) qa = new QuestionAnswer(q.getId());
        Element elQ = XHelper.addXmlQuestion(elQuiz, q);
        Xbuilder.setAttrForce(elQ, "alternative_answer", qa.getAltAnswer());
        Xbuilder.setAttrForce(elQ, "remark", qa.getRemark());
        if(q.getType() == QuestionType.TEXT || q.getType() == QuestionType.TEXTAREA) {
            Xbuilder.setAttrForce(elQ, "answer", qa.getTextAnswer());
        } else {
            List<Option> opts = inquiry.getOptionsByQuestionId(q.getId());
            for (Option opt : opts) {
                Element elOpt = XHelper.addXmlOption(elQ, opt);
                if( qa.getOptionValue(opt.getId()) ) {
                    Xbuilder.setAttrForce(elOpt, "checked", "true");
                }
            }
        }
        logger.debug("-");
        return;
    }
    
    /**
     * Return the next question from list <b>questions</b>. Returns null,
     * if current question is the last in the list.
     */
    private Question getNextQuestion(List<Question> questions) {
        logger.debug("+");
        int qId =  Integer.parseInt(req.getParameter("inquiry_question_id"));
        for (Iterator<Question> it = questions.iterator(); it.hasNext();) {
            Question q = it.next();
            if (q.getId() == qId && it.hasNext()) {
                logger.debug("- next question found");
                return it.next();
            }
        }
        logger.debug("- next question not found");
        return null;
    }
    
    /**
     * Returns the previous question from list <b>questions</b>; never null.
     * Returns the first question, if it is current.
     */
    private Question getPreviousQuestion(List<Question> questions) {
        logger.debug("+");
        int qId =  Integer.parseInt(req.getParameter("inquiry_question_id"));
        for (int i = questions.size() -1; i >= 0; i--) {
            Question q = questions.get(i);
            if (q.getId() == qId) {
                logger.debug("- next question found");
                return questions.get(i > 0 ? i-1 : 0);
            }
        }
        throw new RuntimeException("Current question not found");
    }

    private void sessionSaveAnswer(QuestionAnswer newAnswer) {
        logger.debug("+");
        synchronized (this.getClass()) {
            sessionInitAnswers();
            Map<Integer, List<QuestionAnswer>> map =
                (Map) req.getSession().getAttribute("inquiry_questionnaires");
            List<QuestionAnswer> answers = map.get( new Integer(quiz.getId()) );
            for (QuestionAnswer oldAnswer : answers) {
                if (oldAnswer.getQuestionId()== newAnswer.getQuestionId()) {
                    answers.remove(oldAnswer);
                    break;
                }
            }
            answers.add(newAnswer);
        }
        logger.debug("-");
    }
    
    private QuestionAnswer sessionGetAnswer(int questionId) {
        logger.debug("+");
        synchronized (this.getClass()) {
            sessionInitAnswers();
            Map<Integer, List<QuestionAnswer>> map =
                (Map) req.getSession().getAttribute("inquiry_questionnaires");
            List<QuestionAnswer> answers = map.get( new Integer(quiz.getId()) );
            for (QuestionAnswer answer : answers) {
                if (answer.getQuestionId() == questionId) {
                    logger.debug("- answer found");
                    return answer;
                }
            }
            logger.debug("- answer not found");
            return null;
        }
    }

    /**
     * Prepares data structures to store intermediate results in session.
     * Does nothing if the structures are already present in session.
     */
    private void sessionInitAnswers() {
        logger.debug("+");
        synchronized (this.getClass()) {
            HashMap<Integer, List<QuestionAnswer>> quizes =
                (HashMap) req.getSession().getAttribute("inquiry_questionnaires");
            if (quizes == null) {
                quizes = new HashMap<Integer, List<QuestionAnswer>>();
                req.getSession().setAttribute("inquiry_questionnaires", quizes);
            }
            Integer quizId = new Integer(quiz.getId());
            if (quizes.get(quizId) == null) {
                quizes.put(quizId, new LinkedList<QuestionAnswer>());
            }
        }
        logger.debug("-");
    }
    
}
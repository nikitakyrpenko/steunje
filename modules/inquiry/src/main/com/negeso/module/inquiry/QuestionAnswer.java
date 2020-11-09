package com.negeso.module.inquiry;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.negeso.module.inquiry.domain.Question;
import com.negeso.module.inquiry.domain.QuestionType;

/**
 * Data structure to store in session an answer to a single question
 * of arbitrary type. Only multipage questionnaires need this class.
 */
class QuestionAnswer implements Serializable {
    
    private final int questionId;

    /**
     * Must be empty for text questions
     * 
     * Key: option id<br/>
     * Value: true (checked/selected) or false (unchecked)
     */
    private Map<Integer, Boolean> options = new HashMap<Integer, Boolean>();
    
    private String altAnswer;
    
    /** Must be null for non-text questions */
    private String textAnswer;
    
    private String remark;
    
    public QuestionAnswer(int questionId) {
        this.questionId = questionId;
    }
    
    public boolean isComplete() {
        if (altAnswer != null) return true;
        Question q = Inquiry.getInstance().loadQuestion(questionId);
        if (!q.getRequired()) return true;
        if(q.getType() == QuestionType.TEXT || q.getType() == QuestionType.TEXTAREA) {
            return textAnswer != null;
        }
        for (Boolean val : options.values()) {
            if (val) return true;
        }
        return false;
    }
    
    public void setOptionValue(int optionId, boolean value) {
        options.put(optionId, value);
    }
    
    public boolean getOptionValue(int optionId) {
        return
            options.get(optionId) == null
            ? false
            : Boolean.valueOf(options.get(optionId));
    }

    public String getRemark() { return remark; }
    
    public void setRemark(String remark) {
        this.remark = StringUtils.trimToNull(remark);
    }

    public String getTextAnswer() { return textAnswer; }
    
    public void setTextAnswer(String textAnswer) {
        this.textAnswer = StringUtils.trimToNull(textAnswer);
    }

    public String getAltAnswer() { return altAnswer; }
    
    public void setAltAnswer(String altAnswer) {
        this.altAnswer = StringUtils.trimToNull(altAnswer);
    }

    public int getQuestionId() { return questionId; }
    
    @Override
    public String toString() { return ToStringBuilder.reflectionToString(this); }
    
}

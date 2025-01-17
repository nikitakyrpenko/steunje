package com.negeso.framework;

import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;
import org.springframework.core.Ordered;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;
import java.util.Properties;
import java.util.Enumeration;

/**
 * Created by NEGESO.
 * User: Seriy Vitaliy
 * Date: 27.10.2008
 * Time: 19:37:19
 */
public class ExceptionHandler implements HandlerExceptionResolver, Ordered {

    /**
         * The default name of the exception attribute: "exception".
         */
        public static final String DEFAULT_EXCEPTION_ATTRIBUTE = "exception";
        public static final String ERROR_STATUS_CODE_ATTRIBUTE = "javax.servlet.error.status_code";


        /** Logger available to subclasses */
        protected final Log logger = LogFactory.getLog(getClass());

        private int order = Integer.MAX_VALUE;  // default: same as non-Ordered

        private Set mappedHandlers;

        private Class[] mappedHandlerClasses;

        private Log warnLogger;

        private Properties exceptionMappings;

        private String defaultErrorView;

        private Integer defaultStatusCode;

        private String exceptionAttribute = DEFAULT_EXCEPTION_ATTRIBUTE;


    public ExceptionHandler() {
    }

    public void setOrder(int order) {
        this.order = order;
        }

        public int getOrder() {
          return this.order;
        }

        /**
         * Specify the set of handlers that this exception resolver should apply to.
         * The exception mappings and the default error view will only apply
         * to the specified handlers.
         * <p>If no handlers and handler classes are set, the exception mappings
         * and the default error view will apply to all handlers. This means that
         * a specified default error view will be used as fallback for all exceptions;
         * any further HandlerExceptionResolvers in the chain will be ignored in
         * this case.
         */
        public void setMappedHandlers(Set mappedHandlers) {
            this.mappedHandlers = mappedHandlers;
        }

        /**
         * Specify the set of classes that this exception resolver should apply to.
         * The exception mappings and the default error view will only apply
         * to handlers of the specified type; the specified types may be interfaces
         * and superclasses of handlers as well.
         * <p>If no handlers and handler classes are set, the exception mappings
         * and the default error view will apply to all handlers. This means that
         * a specified default error view will be used as fallback for all exceptions;
         * any further HandlerExceptionResolvers in the chain will be ignored in
         * this case.
         */
        public void setMappedHandlerClasses(Class[] mappedHandlerClasses) {
            this.mappedHandlerClasses = mappedHandlerClasses;
        }

        /**
         * Set the log category for warn logging. The name will be passed to the
         * underlying logger implementation through Commons Logging, getting
         * interpreted as log category according to the logger's configuration.
         * <p>Default is no warn logging. Specify this setting to activate
         * warn logging into a specific category. Alternatively, override
         * the {@link #logException} method for custom logging.
         * @see org.apache.commons.logging.LogFactory#getLog(String)
         * @see org.apache.log4j.Logger#getLogger(String)
         * @see java.util.logging.Logger#getLogger(String)
         */
        public void setWarnLogCategory(String loggerName) {
            this.warnLogger = LogFactory.getLog(loggerName);
        }

        /**
         * Set the mappings between exception class names and error view names.
         * The exception class name can be a substring, with no wildcard support
         * at present. A value of "ServletException" would match
         * <code>javax.servlet.ServletException</code> and subclasses, for example.
         * <p><b>NB:</b> Consider carefully how specific the pattern is, and whether
         * to include package information (which isn't mandatory). For example,
         * "Exception" will match nearly anything, and will probably hide other rules.
         * "java.lang.Exception" would be correct if "Exception" was meant to define
         * a rule for all checked exceptions. With more unusual exception names such
         * as "BaseBusinessException" there's no need to use a FQN.
         * <p>Follows the same matching algorithm as RuleBasedTransactionAttribute
         * and RollbackRuleAttribute.
         * @param mappings exception patterns (can also be fully qualified class names)
         * as keys, and error view names as values
         * @see org.springframework.transaction.interceptor.RuleBasedTransactionAttribute
         * @see org.springframework.transaction.interceptor.RollbackRuleAttribute
         */
        public void setExceptionMappings(Properties mappings) {
            this.exceptionMappings = mappings;
        }

        /**
         * Set the name of the default error view.
         * This view will be returned if no specific mapping was found.
         * <p>Default is none.
         */
        public void setDefaultErrorView(String defaultErrorView) {
            this.defaultErrorView = defaultErrorView;
        }

        /**
         * Set the default HTTP status code that this exception resolver will apply
         * if it resolves an error view.
         * <p>Note that this error code will only get applied in case of a top-level
         * request. It will not be set for an include request, since the HTTP status
         * cannot be modified from within an include.
         * <p>If not specified, no status code will be applied, either leaving this to
         * the controller or view, or keeping the servlet engine's default of 200 (OK).
         * @param defaultStatusCode HTTP status code value, for example
         * 500 (SC_INTERNAL_SERVER_ERROR) or 404 (SC_NOT_FOUND)
         * @see javax.servlet.http.HttpServletResponse#SC_INTERNAL_SERVER_ERROR
         * @see javax.servlet.http.HttpServletResponse#SC_NOT_FOUND
         */
        public void setDefaultStatusCode(int defaultStatusCode) {
            this.defaultStatusCode = new Integer(defaultStatusCode);
        }

        /**
         * Set the name of the model attribute as which the exception should
         * be exposed. Default is "exception".
         * <p>This can be either set to a different attribute name or to
         * <code>null</code> for not exposing an exception attribute at all.
         * @see #DEFAULT_EXCEPTION_ATTRIBUTE
         */
        public void setExceptionAttribute(String exceptionAttribute) {
            this.exceptionAttribute = exceptionAttribute;
        }


        /**
         * Checks whether this resolver is supposed to apply (i.e. the handler
         * matches in case of "mappedHandlers" having been specified), then
         * delegates to the {@link #doResolveException} template method.
         */
        public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
            if (shouldApplyTo(request, handler)) {
                return doResolveException(request, response, handler, ex);
            }
            else {
                return null;
            }
        }

        /**
         * Check whether this resolver is supposed to apply to the given handler.
         * <p>The default implementation checks against the specified mapped handlers
         * and handler classes, if any.
         * @param request current HTTP request
         * @param handler the executed handler, or <code>null</code> if none chosen at the
         * time of the exception (for example, if multipart resolution failed)
         * @return whether this resolved should proceed with resolving the exception
         * for the given request and handler
         * @see #setMappedHandlers
         * @see #setMappedHandlerClasses
         */
        protected boolean shouldApplyTo(HttpServletRequest request, Object handler) {
            if (handler != null) {
                if (this.mappedHandlers != null && this.mappedHandlers.contains(handler)) {
                    return true;
                }
                if (this.mappedHandlerClasses != null) {
                    for (int i = 0; i < this.mappedHandlerClasses.length; i++) {
                        if (this.mappedHandlerClasses[i].isInstance(handler)) {
                            return true;
                        }
                    }
                }
            }
            // Else only apply if there are no explicit handler mappings.
            return (this.mappedHandlers == null && this.mappedHandlerClasses == null);
        }

        /**
         * Actually resolve the given exception that got thrown during on handler execution,
         * returning a ModelAndView that represents a specific error page if appropriate.
         * <p>May be overridden in subclasses, in order to apply specific exception checks.
         * Note that this template method will be invoked <i>after</i> checking whether
         * this resolved applies ("mappedHandlers" etc), so an implementation may simply
         * proceed with its actual exception handling.
         * @param request current HTTP request
         * @param response current HTTP response
         * @param handler the executed handler, or <code>null</code> if none chosen at the
         * time of the exception (for example, if multipart resolution failed)
         * @param ex the exception that got thrown during handler execution
         * @return a corresponding ModelAndView to forward to, or <code>null</code> for default processing
         */
        protected ModelAndView doResolveException(
                HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

            logger.error("Resolving exception from handler [" + handler + "]: " + ex);
            logException(ex, request);

            // Expose ModelAndView for chosen error view.
            String viewName = determineViewName(ex, request);
            if (viewName != null) {
                // Apply HTTP status code for error views, if specified.
                // Only apply it if we're processing a top-level request.
                Integer statusCode = determineStatusCode(request, viewName);
                if (statusCode != null) {
                    applyStatusCodeIfPossible(request, response, statusCode.intValue());
                }
                return getModelAndView(viewName, ex, request);
            }
            else {
                return null;
            }
        }


        /**
         * Log the given exception at warn level, provided that warn logging has been
         * activated through the {@link #setWarnLogCategory "warnLogCategory"} property.
         * <p>Calls {@link #buildLogMessage} in order to determine the concrete message
         * to log. Always passes the full exception to the logger.
         * @param ex the exception that got thrown during handler execution
         * @param request current HTTP request (useful for obtaining metadata)
         * @see #setWarnLogCategory
         * @see #buildLogMessage
         * @see org.apache.commons.logging.Log#warn(Object, Throwable)
         */
        protected void logException(Exception ex, HttpServletRequest request) {
            if (this.warnLogger != null && this.warnLogger.isWarnEnabled()) {
                this.warnLogger.warn(buildLogMessage(ex, request), ex);
            }
        }

        /**
         * Build a log message for the given exception, occured during processing
         * the given request.
         * @param ex the exception that got thrown during handler execution
         * @param request current HTTP request (useful for obtaining metadata)
         * @return the log message to use
         */
        protected String buildLogMessage(Exception ex, HttpServletRequest request) {
            return "Handler execution resulted in exception";
        }


        /**
         * Determine the view name for the given exception, searching the
         * {@link #setExceptionMappings "exceptionMappings"}, using the
         * {@link #setDefaultErrorView "defaultErrorView"} as fallback.
         * @param ex the exception that got thrown during handler execution
         * @param request current HTTP request (useful for obtaining metadata)
         * @return the resolved view name, or <code>null</code> if none found
         */
        protected String determineViewName(Exception ex, HttpServletRequest request) {
            String viewName = null;
            // Check for specific exception mappings.
            if (this.exceptionMappings != null) {
                viewName = findMatchingViewName(this.exceptionMappings, ex);
            }
            // Return default error view else, if defined.
            if (viewName == null && this.defaultErrorView != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Resolving to default view '" + this.defaultErrorView +
                            "' for exception of type [" + ex.getClass().getName() + "]");
                }
                viewName = this.defaultErrorView;
            }
            return viewName;
        }

        /**
         * Find a matching view name in the given exception mappings.
         * @param exceptionMappings mappings between exception class names and error view names
         * @param ex the exception that got thrown during handler execution
         * @return the view name, or <code>null</code> if none found
         * @see #setExceptionMappings
         */
        protected String findMatchingViewName(Properties exceptionMappings, Exception ex) {
            String viewName = null;
            String dominantMapping = null;
            int deepest = Integer.MAX_VALUE;
            for (Enumeration names = exceptionMappings.propertyNames(); names.hasMoreElements();) {
                String exceptionMapping = (String) names.nextElement();
                int depth = getDepth(exceptionMapping, ex);
                if (depth >= 0 && depth < deepest) {
                    deepest = depth;
                    dominantMapping = exceptionMapping;
                    viewName = exceptionMappings.getProperty(exceptionMapping);
                }
            }
            if (viewName != null && logger.isDebugEnabled()) {
                logger.debug("Resolving to view '" + viewName + "' for exception of type [" + ex.getClass().getName() +
                        "], based on exception mapping [" + dominantMapping + "]");
            }
            return viewName;
        }

        /**
         * Return the depth to the superclass matching.
         * <p>0 means ex matches exactly. Returns -1 if there's no match.
         * Otherwise, returns depth. Lowest depth wins.
         * <p>Follows the same algorithm as
         * {@link org.springframework.transaction.interceptor.RollbackRuleAttribute}.
         */
        protected int getDepth(String exceptionMapping, Exception ex) {
            return getDepth(exceptionMapping, ex.getClass(), 0);
        }

        private int getDepth(String exceptionMapping, Class exceptionClass, int depth) {
            if (exceptionClass.getName().indexOf(exceptionMapping) != -1) {
                // Found it!
                return depth;
            }
            // If we've gone as far as we can go and haven't found it...
            if (exceptionClass.equals(Throwable.class)) {
                return -1;
            }
            return getDepth(exceptionMapping, exceptionClass.getSuperclass(), depth + 1);
        }


        /**
         * Determine the HTTP status code to apply for the given error view.
         * <p>The default implementation always returns the specified
         * {@link #setDefaultStatusCode "defaultStatusCode"}, as a common
         * status code for all error views. Override this in a custom subclass
         * to determine a specific status code for the given view.
         * @param request current HTTP request
         * @param viewName the name of the error view
         * @return the HTTP status code to use, or <code>null</code> for the
         * servlet container's default (200 in case of a standard error view)
         * @see #setDefaultStatusCode
         * @see #applyStatusCodeIfPossible
         */
        protected Integer determineStatusCode(HttpServletRequest request, String viewName) {
            return this.defaultStatusCode;
        }

        /**
         * Apply the specified HTTP status code to the given response, if possible
         * (that is, if not executing within an include request).
         * @param request current HTTP request
         * @param response current HTTP response
         * @param statusCode the status code to apply
         * @see #determineStatusCode
         * @see #setDefaultStatusCode
         * @see javax.servlet.http.HttpServletResponse#setStatus
         */
        protected void applyStatusCodeIfPossible(HttpServletRequest request, HttpServletResponse response, int statusCode) {
            if (!WebUtils.isIncludeRequest(request)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Applying HTTP status code " + statusCode);
                }
                response.setStatus(statusCode);
                request.setAttribute(ERROR_STATUS_CODE_ATTRIBUTE, new Integer(statusCode));
            }
        }

        /**
         * Return a ModelAndView for the given request, view name and exception.
         * <p>The default implementation delegates to {@link #getModelAndView(String, Exception)}.
         * @param viewName the name of the error view
         * @param ex the exception that got thrown during handler execution
         * @param request current HTTP request (useful for obtaining metadata)
         * @return the ModelAndView instance
         */
        protected ModelAndView getModelAndView(String viewName, Exception ex, HttpServletRequest request) {
            return getModelAndView(viewName, ex);
        }

        /**
         * Return a ModelAndView for the given view name and exception.
         * <p>The default implementation adds the specified exception attribute.
         * Can be overridden in subclasses.
         * @param viewName the name of the error view
         * @param ex the exception that got thrown during handler execution
         * @return the ModelAndView instance
         * @see #setExceptionAttribute
         */
        protected ModelAndView getModelAndView(String viewName, Exception ex) {
            ModelAndView mv = new ModelAndView(viewName);
            if (this.exceptionAttribute != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Exposing Exception as model attribute '" + this.exceptionAttribute + "'");
                }
                mv.addObject(this.exceptionAttribute, ex);
            }
            return mv;
        }


}

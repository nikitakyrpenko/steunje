package com.negeso.framework.captcha;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.octo.captcha.image.ImageCaptcha;

public class CaptchaController extends AbstractController {

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String captchaInput = request.getParameter("captcha_input");
		ImageCaptcha captcha = (ImageCaptcha) request.getSession().getAttribute("captcha");
		ModelAndView mv = new ModelAndView("captcha_check_helper");
		mv.addObject("isValidCaptchaCode", isValidCaptchaCode(captchaInput, captcha));
		return mv;
	}

	private boolean isValidCaptchaCode(String captchaInput, ImageCaptcha captcha) {
		return captcha != null && captcha.validateResponse(captchaInput);
	}
}

package com.negeso.module.webshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AdminController {

	@RequestMapping({"/admin/webshop", "/admin/webshop/**"})
	public String jsp(){

		return "webshop";
	}
}

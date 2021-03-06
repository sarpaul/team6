package com.staff.web;

import javax.servlet.http.HttpServletRequest;
import com.staff.api.entity.User;
import com.staff.api.enums.Sort.SortOrder;
import com.staff.api.enums.Sort.SortUserFields;
import com.staff.dao.sort.Sort;
import com.staff.dao.specification.EntityRepository.UserSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.staff.api.service.IUserService;
import com.staff.validator.UserFormValidator;

import java.util.List;

@Controller
public class UserController extends BaseController {

	private final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	UserFormValidator userFormValidator;
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.setValidator(userFormValidator);
	}

	private IUserService userService;

	@Autowired
	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Model model) {
		logger.debug("index()");
		return "redirect:/users";
	}

	// list page
	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public String showAllUsers(Model model, @ModelAttribute("userForm")  User user,
							   @RequestParam(value = "columnName", defaultValue ="NAME") String columnName,
							   @RequestParam(value = "order", defaultValue = "ASC") String order,
							   @RequestParam(value = "page", defaultValue = "1") int page,
							   @RequestParam(value = "pagesize", defaultValue = "10") int pagesize) {

		logger.debug("showAllUsers()");

		List<User> Users = userService.FindWithPaging(new UserSpecification().GetByNameLike(user.getName())
				.GetAnd().GetByEmailLike(user.getEmail()).GetAnd().GetBySurnameLike(user.getSurname()),
				new Sort().setColumnName(columnName).setSortOrder(order), page, pagesize);

		int userCount = userService.Count(new UserSpecification());
		int pageCount = userCount/pagesize +1;

		model.addAttribute("users", Users);
		model.addAttribute("userForm", user);
		model.addAttribute("columnName",columnName);
		model.addAttribute("currentOrder",order);
		model.addAttribute("order",order.toUpperCase().equals("ASC") ? "DESC" : "ASC");
		model.addAttribute("pageNumber",page);
		model.addAttribute("pageCount", pageCount);
		return "users/list";

	}

	// save or update user
	@RequestMapping(value = "/users", method = RequestMethod.POST)
	public String saveOrUpdateUser(@ModelAttribute("userForm") @Validated User user,
			BindingResult result, Model model, final RedirectAttributes redirectAttributes) {

		logger.debug("saveOrUpdateUser() : {}", user);

		if (result.hasErrors()) {
			return "users/userform";
		} else {

			redirectAttributes.addFlashAttribute("css", "success");
			if(user.isNew()){
				redirectAttributes.addFlashAttribute("msg", "User added successfully!");
			}else{
				redirectAttributes.addFlashAttribute("msg", "User updated successfully!");
			}
			
			userService.saveOrUpdate(user, new UserSpecification().GetById(user.getForeignKey()));

			return "redirect:/users/" + user.getId();
		}

	}

	// show add user form
	@RequestMapping(value = "/users/add", method = RequestMethod.GET)
	public String showAddUserForm(Model model) {

		logger.debug("showAddUserForm()");

		User user = new User();

		// set default value
		user.setName("TestName");
		user.setEmail("test@mail.ru");
		user.setSurname("TestSurname");

		model.addAttribute("userForm", user);

		return "users/userform";

	}

	// show update form
	@RequestMapping(value = "/users/{id}/update", method = RequestMethod.GET)
	public String showUpdateUserForm(@PathVariable("id") int id, Model model) {

		logger.debug("showUpdateUserForm() : {}", id);

		User user = userService.Read(new UserSpecification().GetById(String.valueOf(id)));
		model.addAttribute("userForm", user);
		
		return "users/userform";

	}

	// delete user
	@RequestMapping(value = "/users/{id}/delete", method = RequestMethod.POST)
	public String deleteUser(@PathVariable("id") int id, final RedirectAttributes redirectAttributes) {

		logger.debug("deleteUser() : {}", id);

		userService.delete(new UserSpecification().GetById(String.valueOf(id)));
		
		redirectAttributes.addFlashAttribute("css", "success");
		redirectAttributes.addFlashAttribute("msg", "User is deleted!");
		
		return "redirect:/users";

	}

	// show user
	@RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
	public String showUser(@PathVariable("id") int id, Model model) {

		logger.debug("showUser() id: {}", id);

		User user = userService.Read(new UserSpecification().GetById(String.valueOf(id)));
		if (user == null) {
			model.addAttribute("css", "danger");
			model.addAttribute("msg", "User not found");
		}
		model.addAttribute("user", user);

		return "users/show";

	}

	@ExceptionHandler(EmptyResultDataAccessException.class)
	public ModelAndView handleEmptyData(HttpServletRequest req, Exception ex) {

		logger.debug("handleEmptyData()");
		logger.error("Request: {}, error ", req.getRequestURL(), ex);

		ModelAndView model = new ModelAndView();
		model.setViewName("user/show");
		model.addObject("msg", "user not found");

		return model;

	}

}
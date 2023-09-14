package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.Util.UserValidator;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;
    private final UserValidator userValidator;

    @Autowired
    public AdminController(UserService userService, RoleService roleService, UserValidator userValidator) {
        this.userService = userService;
        this.roleService = roleService;
        this.userValidator = userValidator;
    }

    @GetMapping()
    public String index(ModelMap model) {
        model.addAttribute("users", userService.allUsers());
        return "admin/index";
    }

    @GetMapping("/new")
    public String newUser(ModelMap model, @ModelAttribute("user") User user) {
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "admin/new";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute("user") @Valid User user,
                         BindingResult bindingResult,
                         @RequestParam(value = "roles", defaultValue = "") Set<Long> roles) {
        userValidator.validate(user.getUsername(), bindingResult);

        if (bindingResult.hasErrors()) {
            return "/admin/new";
        }
        if (roles != null) {
            Set<Role> rolesSet = new HashSet<>();
            for (Long aLong : roles) {
                Role role = new Role();
                role.setId(aLong);
                role.setName((roleService.getRoleById(aLong).getName()));
                rolesSet.add(role);
            }
            user.setAuthorities(rolesSet);
        }

        userService.add(user);
        return "redirect:/admin";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model,
                       @PathVariable("id") long id) {
        model.addAttribute("user", userService.show(id));
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "admin/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("user") User userForm,

                         @RequestParam(name = "selectedRoles", defaultValue = "") List<Long> selectedRoles) {

        Set<Role> roles = new HashSet<>();

        for (Long role : selectedRoles) {
            roles.add(roleService.getRoleById(role));
        }

        userForm.setAuthorities(roles);
        userService.edit(userForm);
        return "redirect:/admin";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") long id) {
        userService.delete(id);
        return "redirect:/admin";
    }
}

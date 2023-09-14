package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.Util.UserValidator;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.service.RegistrationService;
import ru.kata.spring.boot_security.demo.service.RoleService;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping()
public class RegistrationController {

    private final RegistrationService registrationService;
    private final UserValidator userValidator;
    private final RoleService roleService;

    @Autowired
    public RegistrationController(RegistrationService registrationService,
                                  UserValidator userValidator,
                                  RoleService roleService)
    {
        this.registrationService = registrationService;
        this.userValidator = userValidator;
        this.roleService = roleService;
    }

    @GetMapping("/registration")
    public String registrationPage(ModelMap model, @ModelAttribute("user") User user) {
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "registration";
    }

    @PostMapping("/registration")
    public String performRegistration(@ModelAttribute("user") @Valid User user,
                                      BindingResult bindingResult,
                                      @RequestParam(value = "roles", defaultValue = "") Set<Long> roles) {
        userValidator.validate(user.getUsername(), bindingResult);

        if (bindingResult.hasErrors()) {
            return "/registration";
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

        registrationService.register(user);

        return "redirect:/login";
    }
}

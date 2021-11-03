package pl.gralak.librarysystem.appuser;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.gralak.librarysystem.exception.MissingUsernameOrPasswordException;
import pl.gralak.librarysystem.exception.UserAlreadyExistsException;
import pl.gralak.librarysystem.exception.UserNotFoundException;

import static pl.gralak.librarysystem.appuser.Role.ROLE_USER;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class AppUserController
{
    private final AppUserService appUserService;

    @GetMapping("/manage-employees")
    public String manageEmployees(Model model)
    {
        model.addAttribute("listOfEmployees", appUserService.getAllEmployees());
        return "appuser/manage-employees";
    }

    @GetMapping("/manage-users")
    public String manageUsers(Model model)
    {
        return "appuser/manage-users";
    }

    @GetMapping
    public String getUserByUsername(Model model, @RequestParam String username,
                                    RedirectAttributes redirectAttributes)
    {
        try
        {
            model.addAttribute("user", appUserService.getUserByUsername(username));
        } catch (UsernameNotFoundException e)
        {
            redirectAttributes.addFlashAttribute("error",
                    "User with username: " + username + " does not exist");
            return "redirect:/user/manage-users";
        }

        return "appuser/user-profile";
    }

    @GetMapping("/new-employee-form")
    public String showNewEmployeeForm(Model model)
    {
        AppUser employee = new AppUser();
        model.addAttribute("employee", employee);
        return "appuser/new-employee-form";
    }

    @PostMapping("/create-new-employee")
    public String createNewEmployee(@ModelAttribute("employee") AppUser appUser, RedirectAttributes redirectAttributes)
    {
        try{
            appUserService.addEmployee(appUser);
        } catch(UserAlreadyExistsException e)
        {
            redirectAttributes.addFlashAttribute("error",
                    "Employee with username: " + appUser.getUsername() + " already exists");
            return "redirect:/user/new-employee-form";
        } catch(MissingUsernameOrPasswordException e)
        {
            redirectAttributes.addFlashAttribute("error",
                    "Value of username and/or password was null or empty");
            return "redirect:/user/new-employee-form";
        }
        redirectAttributes.addFlashAttribute("success",
                "Employee created!");
        return "redirect:/user/manage-employees";
    }

    @GetMapping("/edit-user-form/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model)
    {
        model.addAttribute("user", appUserService.getUserById(id));
        return "appuser/edit-user-form";
    }

    @PostMapping("/edit-user/{id}")
    public String editUser(@PathVariable Long id, @ModelAttribute("user") AppUser appUser,
                               RedirectAttributes redirectAttributes)
    {
        try{
            appUserService.updateUser(appUser);
        } catch(MissingUsernameOrPasswordException e)
        {
            redirectAttributes.addFlashAttribute("error",
                    "Value of username and/or password was null or empty");
            return "redirect:/user/edit-employee-form/{id}";
        }

        if(appUser.getRole() == ROLE_USER)
        {
            redirectAttributes.addFlashAttribute("success",
                    "User updated!");
            return "redirect:/user/manage-users";
        }
        else
        {
            redirectAttributes.addFlashAttribute("success",
                    "Employee updated!");
            return "redirect:/user/manage-employees";
        }
    }

    @GetMapping("/reset-password-form/{id}")
    public String showResetPasswordForm(@PathVariable Long id, Model model)
    {
        model.addAttribute("id", id);
        model.addAttribute("password", "");
        return "appuser/reset-password-form";
    }

    @PostMapping("/reset-password/{id}")
    public String resetPassword(@PathVariable Long id, @ModelAttribute("password") String password,
                               RedirectAttributes redirectAttributes)
    {
        AppUser appUser;
        try{
            appUser = appUserService.resetPassword(password, id);
        } catch(MissingUsernameOrPasswordException e)
        {
            redirectAttributes.addFlashAttribute("error",
                    "Value of password was null or empty");
            return "redirect:/user/reset-password-form/{id}";
        } catch(UserNotFoundException e)
        {
            redirectAttributes.addFlashAttribute("error",
                    "User does not exist in database");
            return "redirect:/menu";
        }
        redirectAttributes.addFlashAttribute("success",
                "Password has been reset!");
        if(appUser.getRole() == ROLE_USER)
        {
            return "redirect:/user/manage-users";
        }
        else
        {
            return "redirect:/user/manage-employees";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes)
    {
        AppUser appUser;
        try
        {
            appUser = appUserService.deleteUser(id);
        } catch (UserNotFoundException e)
        {
            redirectAttributes.addFlashAttribute("error",
                    "User with given id does not exist");
            return "redirect:/menu";
        }
        if(appUser.getRole() == ROLE_USER)
        {
            redirectAttributes.addFlashAttribute("success",
                    "User deleted!");
            return "redirect:/user/manage-users";
        }
        else
        {
            redirectAttributes.addFlashAttribute("success",
                    "Employee deleted!");
            return "redirect:/user/manage-employees";
        }
    }
}

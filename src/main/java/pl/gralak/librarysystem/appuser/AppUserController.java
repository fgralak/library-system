package pl.gralak.librarysystem.appuser;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.gralak.librarysystem.exception.MissingUsernameOrPasswordException;
import pl.gralak.librarysystem.exception.UserAlreadyExistsException;
import pl.gralak.librarysystem.exception.UserNotFoundException;

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
        return "employee/manage-employees";
    }

    @GetMapping("/new-employee-form")
    public String showNewEmployeeForm(Model model)
    {
        AppUser employee = new AppUser();
        model.addAttribute("employee", employee);
        return "employee/new-employee-form";
    }

    @PostMapping("/create-new-employee")
    public String createNewEmployee(@ModelAttribute("employee") AppUser appUser, RedirectAttributes redirectAttributes)
    {
        try{
            appUserService.addUserByAdmin(appUser);
        } catch(UserAlreadyExistsException e)
        {
            redirectAttributes.addFlashAttribute("error",
                    "User with username: " + appUser.getUsername() + " already exists");
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

    @GetMapping("/edit-employee-form/{id}")
    public String showEditEmployeeForm(@PathVariable Long id, Model model)
    {
        model.addAttribute("employee", appUserService.getUserById(id));
        return "employee/edit-employee-form";
    }

    @PostMapping("/edit-employee/{id}")
    public String editEmployee(@PathVariable Long id, @ModelAttribute("employee") AppUser appUser,
                               RedirectAttributes redirectAttributes)
    {
        try{
            appUserService.updateUserByAdmin(appUser);
        } catch(MissingUsernameOrPasswordException e)
        {
            redirectAttributes.addFlashAttribute("error",
                    "Value of username and/or password was null or empty");
            return "redirect:/user/edit-employee-form/{id}";
        }
        redirectAttributes.addFlashAttribute("success",
                "Employee updated!");
        return "redirect:/user/manage-employees";
    }

    @GetMapping("/reset-password-form/{id}")
    public String showResetPasswordForm(@PathVariable Long id, Model model)
    {
        model.addAttribute("id", id);
        model.addAttribute("password", "");
        return "employee/reset-password-form";
    }

    @PostMapping("/reset-password/{id}")
    public String resetPassword(@PathVariable Long id, @ModelAttribute("password") String password,
                               RedirectAttributes redirectAttributes)
    {
        try{
            appUserService.resetPassword(password, id);
        } catch(MissingUsernameOrPasswordException e)
        {
            redirectAttributes.addFlashAttribute("error",
                    "Value of password was null or empty");
            return "redirect:/user/reset-password-form/{id}";
        } catch(UserNotFoundException e)
        {
            redirectAttributes.addFlashAttribute("error",
                    "User does not exist in database");
            return "redirect:/user/manage-employees";
        }
        redirectAttributes.addFlashAttribute("success",
                "Password has been reset!");
        return "redirect:/user/manage-employees";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes)
    {
        try
        {
            appUserService.deleteUser(id);
        } catch (UserNotFoundException e)
        {
            redirectAttributes.addFlashAttribute("error",
                    "User with given id does not exist");
            return "redirect:/user/manage-employees";
        }
        redirectAttributes.addFlashAttribute("success",
                "Employee deleted!");
        return "redirect:/user/manage-employees";
    }
}

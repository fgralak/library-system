package pl.gralak.librarysystem.appuser;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
        } catch (UserNotFoundException e)
        {
            redirectAttributes.addFlashAttribute("error",
                    "User with username: " + username + ", does not exist");
            return "redirect:/user/manage-users";
        }

        return "appuser/user-profile";
    }

    @GetMapping("/create-form/{type}")
    public String showNewUserForm(Model model, @PathVariable("type") String type)
    {
        AppUser appUser = new AppUser();
        model.addAttribute("appuser", appUser);
        model.addAttribute("type", type);
        return "appuser/new-appuser-form";
    }

    @PostMapping("/create/{type}")
    public String createNewAppUser(@ModelAttribute("appuser") AppUser appUser, @PathVariable("type") String type,
                                    RedirectAttributes redirectAttributes)
    {
        try
        {
            appUserService.addUserByAdminOrEmployee(appUser, type);
        } catch(UserAlreadyExistsException e)
        {
            redirectAttributes.addFlashAttribute("error",
                    type.substring(0, 1).toUpperCase() + type.substring(1) + " with username: "
                            + appUser.getUsername() + ", already exists");
            return "redirect:/user/create-form/{type}";
        } catch(MissingUsernameOrPasswordException e)
        {
            redirectAttributes.addFlashAttribute("error",
                    "Value of username and/or password was null or empty");
            return "redirect:/user/create-form/{type}";
        }

        if(type.equals("user"))
        {
            redirectAttributes.addFlashAttribute("success",
                    "User created!");
            return "redirect:/user/manage-users";
        }
        else
        {
            redirectAttributes.addFlashAttribute("success",
                    "Employee created!");
            return "redirect:/user/manage-employees";
        }
    }

    @GetMapping("/edit-form/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model)
    {
        model.addAttribute("user", appUserService.getUserById(id));
        return "appuser/edit-user-form";
    }

    @PostMapping("/edit/{id}")
    public String editUser(@PathVariable Long id, @ModelAttribute("user") AppUser appUser,
                               RedirectAttributes redirectAttributes)
    {
        try{
            appUserService.updateUser(appUser);
        } catch(MissingUsernameOrPasswordException e)
        {
            redirectAttributes.addFlashAttribute("error",
                    "Value of username and/or password was null or empty");
            return "redirect:/user/edit-form/{id}";
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

    @GetMapping("/reset-password-form/{role}/{id}")
    public String showResetPasswordForm(@PathVariable Long id, @PathVariable Role role, Model model)
    {
        model.addAttribute("role", role);
        model.addAttribute("id", id);
        model.addAttribute("password", "");
        return "appuser/reset-password-form";
    }

    @PostMapping("/reset-password/{role}/{id}")
    public String resetPassword(@PathVariable Long id, @PathVariable Role role,
                                @ModelAttribute("password") String password, RedirectAttributes redirectAttributes)
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
            return "redirect:/menu";
        }
        redirectAttributes.addFlashAttribute("success",
                "Password has been reset!");
        if(role.equals(ROLE_USER))
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

    @GetMapping("/rented-books")
    public String getAllRentedBooksByUser(Model model)
    {
        model.addAttribute("listOfBooks", appUserService.getAllRentedBooksByUser());
        model.addAttribute("role", getRole());
        return "book/book-list";
    }

    @GetMapping("/rented-books-employee")
    public String getAllRentedBooksByUserEmployeeView(Model model, @ModelAttribute("username") String username,
                                                      RedirectAttributes redirectAttributes)
    {
        try
        {
            model.addAttribute("listOfBooks", appUserService.getAllRentedBooksByUsername(username));
        } catch(UserNotFoundException e)
        {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/book/manage-books";
        }

        model.addAttribute("view", "employee");
        model.addAttribute("username", username);
        return "book/book-list";
    }

    private String getRole()
    {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))) ? "ROLE_EMPLOYEE" : "ROLE_USER";
    }
}

package ma.enset.gestionpatient.web;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import ma.enset.gestionpatient.entities.Patient;
import ma.enset.gestionpatient.repository.PatientRepository;
import ma.enset.gestionpatient.service.IHopitalService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@AllArgsConstructor
public class PatientController {
    private final IHopitalService hopitalService;


    @GetMapping("/user/index")
    public String index(Model model,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "4") int size,
                        @RequestParam(defaultValue = "") String search) {

        Page<Patient> patientPage = hopitalService.searchPatientByKeywords(page, size, search);
        model.addAttribute("ListPatients", patientPage.getContent());
        model.addAttribute("pages", new int[patientPage.getTotalPages()]);
        model.addAttribute("currentPage", page);
        model.addAttribute("search",search);
        return "patients";
    }
    @GetMapping("/admin/delete")
    // @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String delete(Long id,String search, int page) {
        hopitalService.deletePatient(id);
        return "redirect:/user/index?page="+page+"&search="+search;
    }
    @GetMapping("/admin/addPatient")
    public String formPatient(Model model) {
        model.addAttribute("patient", new Patient());
        return "FormPatients";
    }
    @PostMapping(path="/admin/save")
    public String save(Model model, @Valid Patient patient, BindingResult bindingResult,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "") String search) {
        if (bindingResult.hasErrors()) return "FormPatients";
        hopitalService.savePatient(patient);
        return "redirect:/user/index?page="+page+"&search="+search;
    }

    @GetMapping("/admin/editPatient")
    public String editPatient(Model model,Long id,String search, int page) {
        Patient patient= hopitalService.findByIDPatient(id);
        if (patient==null) throw new RuntimeException("Patient introuvable");
        model.addAttribute("patient", patient);
        model.addAttribute("currentPage", page);
        model.addAttribute("search", search);
        
        return "editPatient";
    }
    @GetMapping("/")
    public String home(Model model) {
        return "redirect:/user/index";
    }


}

package isis.projet.backend.controller;

import isis.projet.backend.entity.Participation;
import isis.projet.backend.service.ParticipationProjet;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/gestion/participation")
public class ParticipationController {
    private final ParticipationProjet gestionParticipation;
    private final ModelMapper modelMapper;
    public ParticipationController(ParticipationProjet gestionParticipation, ModelMapper modelMapper) {
        this.gestionParticipation = gestionParticipation;
        this.modelMapper = modelMapper;
    }

    /**
     * Ajoute une participation d'un utilisateur a un projet
     *
     * @param dto contient les informations de participation (matricule, code du projet, rôle, pourcentage)
     * @return reponse http avec la participation enregistreé ou un message d'erreur si probleme
     */
    @PostMapping
    public ResponseEntity<?> ajouterParticipation(@RequestBody ParticipationCreationDTO dto) {
        try {
            // appel du service qui gere les participation
            Participation nouvelleParticipation = gestionParticipation.enregistrerParticipation(
                    dto.getMatricule(),
                    dto.getCodeProjet(),
                    dto.getRole(),
                    dto.getPourcentage()
            );
            // conversion en format DTO pour affichage
            ParticipationDTO resultat = modelMapper.map(nouvelleParticipation, ParticipationDTO.class);
            return ResponseEntity.ok(resultat);

        } catch (NoSuchElementException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ApiErrorDTO(e.getMessage()));

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(new ApiErrorDTO("cette personne est déjà inscrite dans ce projet"));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiErrorDTO("une erreur inattendu est survenue : " + e.getMessage()));
        }
    }
}
//test commentaire
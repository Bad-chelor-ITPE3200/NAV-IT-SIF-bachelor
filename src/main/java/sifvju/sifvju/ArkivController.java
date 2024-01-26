package sifvju.sifvju;

import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class ArkivController {

    private final List<ArkivPost> arkivList = new ArrayList<>();

    // Konstruktør som legger til noen mock data i listen
    public ArkivController() {
        arkivList.add(new ArkivPost("1", "Arkivnavn 1", "2024-01-01", true));
        arkivList.add(new ArkivPost("2", "Arkivnavn 2", "2024-01-02", false));
    }

    @QueryMapping
    public List<ArkivPost> arkiv() {
        return arkivList;
    }

    @MutationMapping
    public ArkivPost addArkivPost(String brukerId, String name, String dato, Boolean status) {
        ArkivPost nyPost = new ArkivPost(brukerId, name, dato, status);
        arkivList.add(nyPost);
        return nyPost;
    }


    public static class ArkivPost {
        private String brukerId;
        private String name;
        private String dato;
        private Boolean status;

        public ArkivPost(String brukerId, String name, String dato, Boolean status) {
            this.brukerId = brukerId;
            this.name = name;
            this.dato = dato;
            this.status = status;
        }

        // Gettere
        public String getBrukerId() {
            return brukerId;
        }

        public String getName() {
            return name;
        }

        public String getDato() {
            return dato;
        }

        public Boolean getStatus() {
            return status;
        }

        // Settere
        public void setBrukerId(String brukerId) {
            this.brukerId = brukerId;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setDato(String dato) {
            this.dato = dato;
        }

        public void setStatus(Boolean status) {
            this.status = status;
        }
    }
}

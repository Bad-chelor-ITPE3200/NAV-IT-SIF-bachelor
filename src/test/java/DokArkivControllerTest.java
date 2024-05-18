import com.bachelor.vju_vm_apla2.ErrorHandling.CustomClientException;
import com.bachelor.vju_vm_apla2.Controller.DokArkivController;
import com.bachelor.vju_vm_apla2.Models.DTO.DokArkiv.CreateJournalpost_DTO;
import com.bachelor.vju_vm_apla2.Models.DTO.DokArkiv.ResponeReturnFromDokArkiv_DTO;
import com.bachelor.vju_vm_apla2.Models.DTO.Request.PostOppdaterJournalpost_DTO;
import com.bachelor.vju_vm_apla2.Models.POJO.Dokarkiv.Dokumenter;
import com.bachelor.vju_vm_apla2.Models.POJO.Dokarkiv.Dokumentvariant;
import com.bachelor.vju_vm_apla2.Models.POJO.Saf.Journalposttype;
import com.bachelor.vju_vm_apla2.Service.DokArkiv_Service.FeilRegistrer_DELETE;
import com.bachelor.vju_vm_apla2.Service.DokArkiv_Service.OppdateringAvJournalposter_UPDATE;
import com.bachelor.vju_vm_apla2.Service.DokArkiv_Service.OpprettNyeJournalposter_CREATE;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class DokArkivControllerTest {
    private static final Logger logger = LogManager.getLogger(DokArkivControllerTest.class);
    //mocks for CRUD operations
@Mock
    FeilRegistrer_DELETE feilRegistrer_delete;
@Mock
    OpprettNyeJournalposter_CREATE opprettNyeJournalposter_create;
@Mock
    OppdateringAvJournalposter_UPDATE oppdateringAvJournalposterUpdate;
@InjectMocks
DokArkivController dokArkivController;
//crud operations tests
@Test
    public void createDokArkiv() {


    List<Dokumenter> docs = new ArrayList<>();
    List<Dokumentvariant>varainter = new ArrayList<>();
    varainter.add(new Dokumentvariant());
    docs.add(new Dokumenter("hei", "hallo", varainter ));


    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer");
    CreateJournalpost_DTO journalpost = new CreateJournalpost_DTO();
    ResponeReturnFromDokArkiv_DTO response = new ResponeReturnFromDokArkiv_DTO();
    ResponeReturnFromDokArkiv_DTO response2 = new ResponeReturnFromDokArkiv_DTO();

    List<ResponeReturnFromDokArkiv_DTO> responeList = Arrays.asList(response, response2);
    ResponseEntity<List<ResponeReturnFromDokArkiv_DTO>> re = new ResponseEntity<>(responeList, HttpStatus.OK);
    Mono<ResponseEntity<List<ResponeReturnFromDokArkiv_DTO>>> ret;
    ret = Mono.just(re);

    Mockito.when(opprettNyeJournalposter_create.createJournalpost_Service(journalpost, headers )).thenReturn(ret);

    Mono<ResponseEntity<List<ResponeReturnFromDokArkiv_DTO>>>res = dokArkivController.createJournalpost_Controller(journalpost, headers);
    Assert.assertEquals(ret.doOnSuccess(response1 -> logger.info("DokArkivController - createJournalpost() - Success - Response sent to client: {}", response1.getStatusCode())).toString(), res.toString());
}
@Test
    public void updateDokArkiv() {
    List<Dokumenter> docs = new ArrayList<>();
    List<Dokumentvariant>varainter = new ArrayList<>();
    varainter.add(new Dokumentvariant());
    docs.add(new Dokumenter("hei", "hallo", varainter ));


    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer");
   PostOppdaterJournalpost_DTO jp = new PostOppdaterJournalpost_DTO();
    PostOppdaterJournalpost_DTO jp2 = new PostOppdaterJournalpost_DTO();

    ResponseEntity<Boolean> re = new ResponseEntity<>(true, HttpStatus.OK);
    Mono<ResponseEntity<Boolean>> ret;
    ret = Mono.just(re);
   Mockito.when(oppdateringAvJournalposterUpdate.oppdaterMottattDato(jp, headers)).thenReturn(ret);

   Mono<ResponseEntity<Boolean>>res = dokArkivController.oppdaterJournalpost(jp2,headers);
   Assert.assertEquals(ret.doOnSuccess(response1->logger.info("Response sent to client: {}", response1.getStatusCode())).toString(), res.toString());
}

@Test
    public void deleteDokArkiv() {
    ResponseEntity<Boolean> response = new ResponseEntity<>(true, HttpStatus.OK);
    Mono<ResponseEntity<Boolean>>OK = Mono.just(response);
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer");


    Mockito.when(feilRegistrer_delete.feilRegistrer_Service("11", String.valueOf(Journalposttype.U), headers)).thenReturn(OK);

     Mono<ResponseEntity<Boolean>> results = dokArkivController.feilregistrer_Controller("11", Journalposttype.U.toString(), headers);

     Assert.assertEquals(OK.doOnSuccess(response1 -> logger.info("shalabim" + response1.getStatusCode())).toString(), results.toString());


}

@Test(expected = CustomClientException.class)
    public void createDokArkiv_exception() {
    List<Dokumenter> docs = new ArrayList<>();
    List<Dokumentvariant>varainter = new ArrayList<>();
    varainter.add(new Dokumentvariant());
    docs.add(new Dokumenter("hei", "hallo", varainter ));


    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer");

    CreateJournalpost_DTO journalpost = new CreateJournalpost_DTO();
    ResponeReturnFromDokArkiv_DTO response = new ResponeReturnFromDokArkiv_DTO();

    List<ResponeReturnFromDokArkiv_DTO> responeList = Arrays.asList(response);
    ResponseEntity<List<ResponeReturnFromDokArkiv_DTO>> re = new ResponseEntity<>(responeList, HttpStatus.OK);


    Mockito.when(opprettNyeJournalposter_create.createJournalpost_Service(journalpost, headers )).thenThrow(new CustomClientException(404, "hjelp", "hei"));

    Mono<ResponseEntity<List<ResponeReturnFromDokArkiv_DTO>>>res = dokArkivController.createJournalpost_Controller(journalpost, headers);
    StepVerifier.create(res).expectErrorMatches(throwable -> throwable instanceof CustomClientException &&
            throwable.getMessage().contains("hjelp")).verify();
}

}

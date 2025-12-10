package idhub.sortinparcels.auth;


import idhub.sortinparcels.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

//тот обьект который мы вернем нашому клиенту после успешной аутентификацию
@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String role;
}

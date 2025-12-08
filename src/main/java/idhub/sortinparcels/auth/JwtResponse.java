package idhub.sortinparcels.auth;


import idhub.sortinparcels.model.User;

//тот обьект который мы вернем нашому клиенту после успешной аутентификацию
public class JwtResponse {
    private String token;
    private User.Role role;

    public JwtResponse(String token, User.Role role) {
        this.token = token;
        this.role = role;
    }




}

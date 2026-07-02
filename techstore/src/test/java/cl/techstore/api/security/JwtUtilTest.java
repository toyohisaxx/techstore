package cl.techstore.api.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias para JwtUtil.
 * No requiere contexto de Spring ni conexión a base de datos,
 * por lo que puede ejecutarse de forma segura en el pipeline de CI/CD.
 */
class JwtUtilTest {

    @Test
    void generarToken_devuelveTokenNoNulo() {
        String token = JwtUtil.generarToken("admin@techstore.cl");

        assertNotNull(token);
        assertTrue(token.length() > 0, "El token generado no debe estar vacío");
    }

    @Test
    void validarToken_devuelveElUsernameOriginal() {
        String username = "admin@techstore.cl";
        String token = JwtUtil.generarToken(username);

        String usernameExtraido = JwtUtil.validarToken(token);

        assertEquals(username, usernameExtraido);
    }

    @Test
    void validarToken_conTokenInvalido_lanzaExcepcion() {
        String tokenInvalido = "esto.no.es.un.jwt.valido";

        assertThrows(Exception.class, () -> JwtUtil.validarToken(tokenInvalido));
    }
}

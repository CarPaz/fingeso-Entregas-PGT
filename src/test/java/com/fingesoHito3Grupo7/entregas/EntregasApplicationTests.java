package com.fingesoHito3Grupo7.entregas;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"app.jwt.secret=clave-jwt-exclusiva-para-pruebas-automatizadas-123456",
		"spring.datasource.url=jdbc:h2:mem:entregas_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"app.usuarios.csv-path=classpath:usuarios-inexistentes.csv"
})
class EntregasApplicationTests {

	@Test
	void contextLoads() {
	}

}

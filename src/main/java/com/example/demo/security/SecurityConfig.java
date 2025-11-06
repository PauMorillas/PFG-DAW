package com.example.demo.security;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.demo.model.dto.DominioDTO;
import com.example.demo.service.DominioService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	// 1. INYECTAR EL REPOSITORIO DE DOMINIOS
	@Autowired(required = false) // Usamos required=false por si no está implementado al inicio
	private DominioService dominioService;

	// 1. Define el encriptador de contraseñas
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// 3. Define la cadena de filtros de seguridad (Autorización)
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http

				.headers(headers -> headers
						// a) Deshabilitar X-Frame-Options (para que no interfiera con CSP)
						.frameOptions(frameOptions -> frameOptions.disable())

						// b) Aplicar Content-Security-Policy (CSP)
						.contentSecurityPolicy(csp -> csp
								// El método .policy() toma directamente el String de la política
								.policyDirectives(buildCspPolicy())))
				// Autorización de peticiones
				.authorizeHttpRequests(authorize -> authorize

						// 1. Rutas públicas sin autenticación (deben ir primero)
						// Incluye: Confirmación de reservas, registro, API de calendario,
						// manejo de errores de Spring y archivos estáticos.
						.requestMatchers("/public/**", "/register", "/public/api/calendario/**",
								"/public/reservas/confirmar/", "/mail/**", "/css/**",
								"/js/**", "/login", "/error", // <-- Necesario para evitar bucles de seguridad
								"/favicon.ico" // <-- Necesario para archivos de navegación
						).permitAll()

						// 2. Ruta protegida para el dashboard de gestión (gerentes)
						.requestMatchers("/", "/home", "/dashboard/**").hasRole("GESTOR")

						// 3. Permisos generales: cualquier otra petición requiere autenticación
						.anyRequest().authenticated())

				// Deshabilitar formulario de login por defecto
				.formLogin(form -> form.disable())
				
				// Deshabilitar logout por defecto
				.logout(logout -> logout.disable())

				/* .formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/home", true) // Redirecciona al home
																								// después del login
																								// exitoso
						.permitAll()) */

				// Configuración de cierre de sesión
				/* .logout(logout -> logout.permitAll()) */

				// Deshabilitar CSRF para rutas específicas
				.csrf(csrf -> csrf.ignoringRequestMatchers("/mail/**", "/public/**"));

		return http.build();
	}

	// Método que genera la política CSP dinámicamente
	// TODO: El método buildCspPolicy debe ser modificado para que consulte a la API
	// en Laravel
	private String buildCspPolicy() {

		// --- LÓGICA DE DELEGACIÓN ---

		List<DominioDTO> allowedDomains = dominioService.getDominios();
		String allowedDomainsString = "";

		// La lista de dominios será vacía si el servicio falló o si Laravel no tiene
		// datos
		if (!allowedDomains.isEmpty()) {
			allowedDomainsString = allowedDomains.stream()
					.map(DominioDTO::getNombre) // Esto es un method reference 
					.collect(Collectors.joining(" ")); // Espacio como separador
		}

		// Si la lista de dominios está vacía, solo se usa 'self' por seguridad
		String finalDomains = allowedDomainsString.isEmpty() ? "'self'"
				: "'self' " + allowedDomainsString;

		// Directivas CSP
		String frameAncestorsDirective = "frame-ancestors " + finalDomains + " *;";
		String defaultSources = "default-src 'self';";
		String styleSources = "style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net;";
		String scriptSources = "script-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net;";
		String fontSources = "font-src 'self' https://cdn.jsdelivr.net data:;";

		return frameAncestorsDirective +
				defaultSources +
				styleSources +
				scriptSources +
				fontSources;
	}
}

package com.peoplesync.api.config;

import com.peoplesync.api.enums.Rol;
import com.peoplesync.api.enums.TipoFichaje;
import com.peoplesync.api.models.Anuncio;
import com.peoplesync.api.models.Delegacion;
import com.peoplesync.api.models.Fichaje;
import com.peoplesync.api.models.Usuario;
import com.peoplesync.api.repositories.AnuncioRepository;
import com.peoplesync.api.repositories.DelegacionRepository;
import com.peoplesync.api.repositories.FichajeRepository;
import com.peoplesync.api.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final DelegacionRepository delegacionRepository;
    private final FichajeRepository fichajeRepository;
    private final AnuncioRepository anuncioRepository;
    private final PasswordEncoder passwordEncoder;
    private final Random random = new Random();

    // Diccionarios para generar nombres realistas
    private final String[] NOMBRES = {"Carlos", "María", "Juan", "Laura", "Pedro", "Ana", "David", "Carmen", "Javier", "Sara", "Daniel", "Elena", "Alejandro", "Lucía", "Víctor", "Marta", "Hugo", "Paula", "Diego", "Cristina"};
    private final String[] APELLIDOS = {"García", "López", "Martínez", "Sánchez", "Pérez", "Gómez", "Martín", "Jiménez", "Ruiz", "Hernández", "Díaz", "Moreno", "Muñoz", "Álvarez", "Romero", "Alonso", "Gutiérrez", "Navarro", "Torres", "Domínguez"};

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (usuarioRepository.count() > 0) {
            // Si ya hay usuarios pero no anuncios, los añadimos
            if (anuncioRepository.count() == 0) {
                Usuario admin = usuarioRepository.findAll().stream()
                        .filter(u -> u.getRol() == Rol.ADMIN)
                        .findFirst().orElse(null);
                if (admin != null) {
                    sembrarAnuncios(admin);
                }
            }
            System.out.println("🌱 BBDD ya poblada. Omitiendo Data Seeder.");
            return;
        }

        System.out.println("🚀 Iniciando inyección masiva de datos (3 Meses / 30 Empleados)...");
        String passwordComun = passwordEncoder.encode("123456");

        // 1. CREAR 3 DELEGACIONES
        Delegacion madrid = delegacionRepository.save(Delegacion.builder().nombre("Sede Central - Madrid").direccion("Paseo de la Castellana, 15").build());
        Delegacion barcelona = delegacionRepository.save(Delegacion.builder().nombre("Oficina Este - Barcelona").direccion("Avenida Diagonal, 120").build());
        Delegacion valencia = delegacionRepository.save(Delegacion.builder().nombre("Oficina Sur - Valencia").direccion("Calle Colón, 45").build());

        // 2. CREAR ADMINISTRADOR GLOBAL
        Usuario adminGlobal = crearUsuario("00000000A", "Admin Supremo", "admin@peoplesync.com", Rol.ADMIN, madrid, null, passwordComun);

        // 3. CREAR 3 MANAGERS (Uno por delegación)
        Usuario managerMadrid = crearUsuario("11111111B", "Roberto Director", "roberto.manager@peoplesync.com", Rol.MANAGER, madrid, null, passwordComun);
        Usuario managerBarcelona = crearUsuario("22222222C", "Silvia Jefa", "silvia.manager@peoplesync.com", Rol.MANAGER, barcelona, null, passwordComun);
        Usuario managerValencia = crearUsuario("33333333D", "Fernando Líder", "fernando.manager@peoplesync.com", Rol.MANAGER, valencia, null, passwordComun);

        List<Usuario> todosLosEmpleadosYManagers = new ArrayList<>();
        todosLosEmpleadosYManagers.add(managerMadrid);
        todosLosEmpleadosYManagers.add(managerBarcelona);
        todosLosEmpleadosYManagers.add(managerValencia);

        // 4. CREAR 27 EMPLEADOS (9 por delegación)
        int contadorDni = 10000000;
        for (int i = 1; i <= 27; i++) {
            String nombreCompleto = generarNombreAleatorio();
            String email = "empleado" + i + "@peoplesync.com";
            String dni = String.format("%08d%c", contadorDni++, (char) ('A' + random.nextInt(26)));

            Delegacion delegacionAsignada;
            Usuario managerAsignado;

            // Reparto equitativo (1-9 a Madrid, 10-18 a BCN, 19-27 a Valencia)
            if (i <= 9) {
                delegacionAsignada = madrid;
                managerAsignado = managerMadrid;
            } else if (i <= 18) {
                delegacionAsignada = barcelona;
                managerAsignado = managerBarcelona;
            } else {
                delegacionAsignada = valencia;
                managerAsignado = managerValencia;
            }

            Usuario emp = crearUsuario(dni, nombreCompleto, email, Rol.USER, delegacionAsignada, managerAsignado, passwordComun);
            todosLosEmpleadosYManagers.add(emp);
        }

        System.out.println("👥 Usuarios creados. Generando 90 días de fichajes...");

        // 5. GENERAR FICHAJES (3 Meses = 90 días) PARA TODOS
        generarFichajesMasivos(todosLosEmpleadosYManagers, 90);

        // 6. CREAR ANUNCIOS INICIALES
        sembrarAnuncios(adminGlobal);

        System.out.println("✅ ¡Inyección de datos completada con éxito! Ya puedes iniciar sesión.");
        System.out.println("👉 Admin: admin@peoplesync.com / 123456");
        System.out.println("👉 Manager (Ej): roberto.manager@peoplesync.com / 123456");
        System.out.println("👉 Empleado (Ej): empleado1@peoplesync.com / 123456");
    }

    private void sembrarAnuncios(Usuario autor) {
        if (anuncioRepository.count() > 0) return;
        
        Anuncio anuncio1 = Anuncio.builder()
                .titulo("Cierre de Nóminas - " + LocalDate.now().getMonth().toString())
                .contenido("Recuerda revisar tus fichajes antes del día 25 para evitar retrasos en el pago de nóminas.")
                .categoria("NOMINAS")
                .fechaPublicacion(LocalDateTime.now().minusHours(2))
                .autor(autor)
                .activo(true)
                .build();
                
        Anuncio anuncio2 = Anuncio.builder()
                .titulo("Bienvenida a los nuevos fichajes")
                .contenido("Demos una cálida bienvenida a los 3 nuevos ingenieros que se incorporan a la delegación Central.")
                .categoria("GENERAL")
                .fechaPublicacion(LocalDateTime.now().minusDays(1))
                .autor(autor)
                .activo(true)
                .build();
                
        anuncioRepository.saveAll(List.of(anuncio1, anuncio2));
        System.out.println("📢 Anuncios iniciales creados.");
    }

    private Usuario crearUsuario(String dni, String nombre, String email, Rol rol, Delegacion delegacion, Usuario manager, String passwordHash) {
        Usuario user = Usuario.builder()
                .dni(dni)
                .nombreCompleto(nombre)
                .email(email)
                .passwordHash(passwordHash)
                .rol(rol)
                .delegacion(delegacion)
                .manager(manager)
                .diasVacacionesAnuales(22)
                .activo(true)
                .build();
        return usuarioRepository.save(user);
    }

    private String generarNombreAleatorio() {
        String nombre = NOMBRES[random.nextInt(NOMBRES.length)];
        String apellido1 = APELLIDOS[random.nextInt(APELLIDOS.length)];
        String apellido2 = APELLIDOS[random.nextInt(APELLIDOS.length)];
        return nombre + " " + apellido1 + " " + apellido2;
    }

    private void generarFichajesMasivos(List<Usuario> usuarios, int diasAtras) {
        List<Fichaje> fichajesBatch = new ArrayList<>();
        LocalDate hoy = LocalDate.now();

        for (Usuario u : usuarios) {
            for (int i = diasAtras; i >= 0; i--) {
                LocalDate dia = hoy.minusDays(i);

                // Saltarse fines de semana
                if (dia.getDayOfWeek().getValue() >= 6) continue;

                // Bloque Mañana (Entrada ~08:00, Salida ~13:30)
                LocalTime entradaManana = LocalTime.of(8, random.nextInt(15)); // Entre 08:00 y 08:14
                LocalTime salidaManana = LocalTime.of(13, 30 + random.nextInt(15)); // Entre 13:30 y 13:44

                fichajesBatch.add(Fichaje.builder()
                        .usuario(u)
                        .tipo(TipoFichaje.PRESENCIAL)
                        .fechaHoraEntrada(LocalDateTime.of(dia, entradaManana))
                        .fechaHoraSalida(LocalDateTime.of(dia, salidaManana))
                        .ipRegistro("10.0.0." + random.nextInt(255))
                        .build());

                // Bloque Tarde (Entrada ~14:30, Salida ~17:00)
                LocalTime entradaTarde = LocalTime.of(14, 30 + random.nextInt(10)); // Entre 14:30 y 14:39
                LocalTime salidaTarde = LocalTime.of(17, random.nextInt(30)); // Entre 17:00 y 17:29

                // Si es hoy, simulamos que el último empleado no ha cerrado el turno (dejamos salida nula)
                if (i == 0 && random.nextBoolean()) {
                    fichajesBatch.add(Fichaje.builder()
                            .usuario(u)
                            .tipo(TipoFichaje.PRESENCIAL)
                            .fechaHoraEntrada(LocalDateTime.of(dia, entradaTarde))
                            .fechaHoraSalida(null) // Turno en curso
                            .ipRegistro("10.0.0." + random.nextInt(255))
                            .build());
                } else {
                    fichajesBatch.add(Fichaje.builder()
                            .usuario(u)
                            .tipo(TipoFichaje.PRESENCIAL)
                            .fechaHoraEntrada(LocalDateTime.of(dia, entradaTarde))
                            .fechaHoraSalida(LocalDateTime.of(dia, salidaTarde))
                            .ipRegistro("10.0.0." + random.nextInt(255))
                            .build());
                }

                // Guardamos en lotes de 1000 para no saturar la memoria RAM de Spring Boot
                if (fichajesBatch.size() >= 1000) {
                    fichajeRepository.saveAll(fichajesBatch);
                    fichajesBatch.clear();
                }
            }
        }
        // Guardar los restantes
        if (!fichajesBatch.isEmpty()) {
            fichajeRepository.saveAll(fichajesBatch);
        }
    }
}
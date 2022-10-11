package com.talodu.taloduspringboot;

import com.talodu.taloduspringboot.model.Role;
import com.talodu.taloduspringboot.model.User;
import com.talodu.taloduspringboot.model.UserImage;
import com.talodu.taloduspringboot.repository.UserRepository;
import com.talodu.taloduspringboot.server.Service.ServerService;
import com.talodu.taloduspringboot.server.enumeration.Status;
import com.talodu.taloduspringboot.server.model.Server;
import com.talodu.taloduspringboot.server.repository.ServerRepository;
import com.talodu.taloduspringboot.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

//Server logs at: tail -n 200 /opt/tomcat/logs/catalina.out

@SpringBootApplication
public class TaloduspringbootApplication extends SpringBootServletInitializer implements CommandLineRunner {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private ServerRepository serverRepository;

	@Autowired
	private ServerService serverService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(TaloduspringbootApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder springApplicationBuilder) {
		return springApplicationBuilder.sources(TaloduspringbootApplication.class);
	}

	@Override
	public void run(String... args) throws Exception {
		try {
			this.userService.saveRole(new Role(1L, "ROLE_USER"));
			this.userService.saveRole(new Role(2L, "ROLE_MANAGER"));
			this.userService.saveRole(new Role(3L, "ROLE_ADMIN"));
			this.userService.saveRole(new Role(4L, "ROLE_SUPER_ADMIN"));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}


		final Collection<Role> userroles = new ArrayList<>();
		userroles.add(new Role(1L,"USER"));


		final String jtpas = passwordEncoder.encode("Jt.pas12");
		final Date created_at = new Date();
		final Date updated_at = new Date();


		serverRepository.save(
				new Server(1L, "10.0.0.1",
						"Ubuntu Linux",
						"16 GB",
						"Personal PC",
						"http://localhost:8086/images/server1.png",
						Status.SERVER_UP
				)
		);

		serverRepository.save(
				new Server(2L, "10.0.0.2",
						"Windows server",
						"36 GB",
						"Personal PC",
						"http://localhost:8086/images/server2.png",
						Status.SERVER_UP
				)
		);

		serverRepository.save(
				new Server(3L, "1.1.1.1",
						"Centos server",
						"36 GB",
						"Data Server",
						"http://localhost:8086/images/server3.png",
						Status.SERVER_UP
				)
		);




		 User jt = new User(
		 		1L,
				 "Pascal",
				 "jt",
				 "jtene@yahoo.com",
				 jtpas,
				 "jtene@yahoo.com",
				 new ArrayList<>(),
				 "pascal4.png",
				 LocalDate.of(1978, Month.MARCH, 10),
				 new ArrayList<>(),
				 created_at,
				 updated_at

				 );

		//long id, String firstName, String lastName, String email, String password, String username,

		 User bt = new User(
				2L,
				"Brian",
				"tj",
				"btene@yahoo.com",
				 jtpas,
				 "btene@yahoo.com",
				new ArrayList<>(),
				"bria44picts.jpg",
				LocalDate.of(2008, Month.JANUARY, 04),
				new ArrayList<>(),
				 updated_at,
				 updated_at
				);

		User dj = new User(
				3L,
				"Dylan",
				"WJ",
				"dtene@yahoo.com",
				jtpas,
				"dtene@yahoo.com",
				new ArrayList<>(),
				"dylan44pic.jpg",
				LocalDate.of(2011, Month.SEPTEMBER, 19),
				new ArrayList<>(),
				updated_at,
				updated_at
		);



		User dav = new User(
				4L,
				"Dav",
				"J",
				"djtene@yahoo.com",
				jtpas,
				"djtene@yahoo.com",
				new ArrayList<>(),
				"dav44pict.png",
				LocalDate.of(2018, 7, 5),
				new ArrayList<>(),
				updated_at,
				updated_at
		);

		User jj = new User(
				5L,
				"Jayden",
				"Jt",
				"jjtene@yahoo.com",
				jtpas,
				"jjtene@yahoo.com",
				new ArrayList<>(),
				"Jayden.png",
				LocalDate.of(2021, 8, 29),
				new ArrayList<>(),
				updated_at,
				updated_at
		);



		try {
			this.userRepository.save(jt);
			this.userRepository.save(bt);
			this.userRepository.save(dj);
			this.userRepository.save(jj);
			this.userRepository.save(dav);
			//this.userRepository.save(bt2);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		this.userService.addRoleToUser("jtene@yahoo.com", "ROLE_SUPER_ADMIN");
		this.userService.addRoleToUser("jtene@yahoo.com", "ROLE_ADMIN");
		this.userService.addRoleToUser("btene@yahoo.com", "ROLE_ADMIN");
		this.userService.addRoleToUser("dtene@yahoo.com", "ROLE_USER");

	}


}

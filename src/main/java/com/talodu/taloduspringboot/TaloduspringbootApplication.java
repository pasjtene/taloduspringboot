package com.talodu.taloduspringboot;

import com.talodu.taloduspringboot.model.Role;
import com.talodu.taloduspringboot.model.User;
import com.talodu.taloduspringboot.repository.UserRepository;
import com.talodu.taloduspringboot.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;

//Server logs at: tail -n 200 /opt/tomcat/logs/catalina.out

@SpringBootApplication
public class TaloduspringbootApplication extends SpringBootServletInitializer implements CommandLineRunner {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

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
			this.userService.saveRole(new Role(null, "ROLE_USER"));
			this.userService.saveRole(new Role(null, "ROLE_MANAGER"));
			this.userService.saveRole(new Role(null, "ROLE_ADMIN"));
			this.userService.saveRole(new Role(null, "ROLE_SUPER_ADMIN"));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}


		final Collection<Role> userroles = new ArrayList<>();
		userroles.add(new Role(1L,"USER"));


		final String jtpas = passwordEncoder.encode("Jt.pas12");
		final User jt = new User(1L,"Pascal", "jt", "jtene@yahoo.com","jtene@yahoo.com",
				jtpas, new ArrayList<>(), "");

		final User bt = new User( 2L,"Brian", "tj", "btene@yahoo.com",
				"btene@yahoo.com",jtpas, new ArrayList<>(), "");

		try {
			this.userRepository.save(jt);
			this.userRepository.save(bt);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}



		//this.userService.saveUser(new User( "Brian", "tj", "btene2@yahoo.com",
				//"btene@yahoo.com",jtpas, new ArrayList<>(), ""));


		this.userService.addRoleToUser("jtene@yahoo.com", "ROLE_SUPER_ADMIN");
		this.userService.addRoleToUser("jtene@yahoo.com", "ROLE_ADMIN");
		this.userService.addRoleToUser("btene@yahoo.com", "ROLE_ADMIN");

	}


}

package com.talodu.taloduspringboot;

import com.talodu.taloduspringboot.Testing.SimpleCalculator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TaloduspringbootApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void twoPlusTwoShouldEqualFour() {
		var calculator = new SimpleCalculator();
		assertEquals(4, calculator.add(2,2));
	}

}

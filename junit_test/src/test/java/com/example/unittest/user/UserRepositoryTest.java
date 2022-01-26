package com.example.unittest.user;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Description;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.example.unittest.schedule.Schedule;
import com.example.unittest.schedule.ScheduleRepository;
import com.example.unittest.schedule.VoucherMatching;
import com.example.unittest.schedule.VoucherMatchingRepository;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class UserRepositoryTest {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ScheduleRepository scheduleRepository;
	@Autowired
	private VoucherMatchingRepository voucherMatchingRepository;

	@BeforeEach
	void init(){
		User t = Trainer.builder()
			.name("트레이너1")
			.age(30)
			.role(Role.TRAINER)
			.build();

		User c = Client.builder()
			.name("회원1")
			.age(23)
			.role(Role.CLIENT)
			.build();

		VoucherMatching v = VoucherMatching.builder()
			.memo("메모")
			.build();

		User saveTrainer = userRepository.saveAndFlush(t);
		userRepository.saveAndFlush(c);

		v.setTrainer((Trainer)saveTrainer);

		voucherMatchingRepository.save(v);
	}

	@Transactional
	@Test
	void saveScheduleList(){
		Optional<User> optionalTrainer = userRepository.findById(1L);
		Trainer trainer = (Trainer)optionalTrainer.get();
		Optional<User> optionalClient = userRepository.findById(2L);
		Client client = (Client)optionalClient.get();

		LocalDateTime startDateTime = LocalDateTime.now();
		LocalDateTime endDateTime = LocalDateTime.now().plusHours(1);

		VoucherMatching trainerVoucherMatching = trainer.getVoucherMatching();

		Schedule newSchedule = Schedule.builder()
			.startDateTime(startDateTime)
			.endDateTime(endDateTime)
			.title("첫번째 스케줄")
			.build();
		newSchedule.setTrainer(trainer);
		newSchedule.setClient(client);
		newSchedule.setVoucherMatching(trainerVoucherMatching);

		System.out.println("저장전");
		scheduleRepository.save(newSchedule);
		System.out.println("저장후");
		assertEquals(newSchedule.getId(),1);
	}

}
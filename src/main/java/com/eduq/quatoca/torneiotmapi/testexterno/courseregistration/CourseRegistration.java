package com.eduq.quatoca.torneiotmapi.testexterno.courseregistration;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;

@Data
@Entity
public class CourseRegistration {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "student_id")
	Student student;
	
	@ManyToOne
	@JoinColumn(name = "course_id")
	Course course;
	
	LocalDateTime registeredAt;
	
	int grade;
}

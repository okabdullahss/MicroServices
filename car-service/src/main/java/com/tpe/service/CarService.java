package com.tpe.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.tpe.CarDTO;
import com.tpe.controller.request.CarRequest;
import com.tpe.domain.Car;
import com.tpe.exception.ResourceNotFoundException;
import com.tpe.repository.CarRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CarService {
	
	private ModelMapper modelMapper;
	private CarRepository carRepository;
	
	//----------------------- saveCar--------------------------------
	 public void saveCar(CarRequest carRequest) {
		 
		  Car car = modelMapper.map(carRequest, Car.class);
		  carRepository.save(car);		 
	 }

	 
	 //---------------------getAllCars--------------------------------------
 
	 public List<CarDTO> getAllCars(){
		 List<Car> carList = carRepository.findAll();
		 return carList.stream().map(this::mapCarToCarDTO).collect(Collectors.toList());
		 
		 
	 }
	 /* Private method to convert cars into carDTO's */ 
	 private CarDTO mapCarToCarDTO(Car car) {
		 CarDTO carDTO = modelMapper.map(car, CarDTO.class);
		 return carDTO;
	 }
	 
	//---------------------get a car by id--------------------------------------

	 public CarDTO getById(Long id) {
		 Car car = carRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Car not found with id "+id));
		 CarDTO carDTO = mapCarToCarDTO(car);
		 return carDTO;
		 
	 }
	 

}

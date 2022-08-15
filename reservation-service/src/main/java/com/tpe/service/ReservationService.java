package com.tpe.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.tpe.CarDTO;
import com.tpe.ReservationDTO;
import com.tpe.controller.request.ReservationRequest;
import com.tpe.domain.Reservation;
import com.tpe.enums.ReservationStatus;
import com.tpe.exception.ResourceNotFoundException;
import com.tpe.repository.ReservationRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ReservationService {
	
	private EurekaClient eurekaClient;      //rez yaparken, car'ın olup olmadığını kontrol etcem. e car servis yok nası yapcam ? işte bu iki
	private RestTemplate restTemplate;//variable'ı carService'e ulaşmak için yazdım(restTemplate com.tpe içinde bean oluşturdum)
									// bu iki variable sayesinde benim rez servisim, car servisin client'ı olarak çalışacak ve ona ulaşck
								//EUREKA CLIENT = CAR SERVIS'IN YERINI BULMAK ICIN
								// RESTTEMPLATE = SERVISLERIN KENDI ARASINDA ILETISIM KURABİLMESİ İÇİN GEREKEN WEB CLIENT. WEB CLI OLMAZSA
								//SERVISLER KENDI ARASINDA CLIENT İLİŞKİSİ KURAMAZLAR
	
	private ReservationRepository reservationRepository;
	private ModelMapper modelMapper;
	
	//--------------------create reservation -------------------------------------------------
	
	//burada client üzerinden car-service'imin lokasyonunu buluyorum(car servis yml dosyasında car-service isimli servis) ve ondan
	//yararlanacak olan reservation servis client'ı
	
	public void saveReservation(Long carId, ReservationRequest reservationRequest) {
		InstanceInfo instanceInfo = eurekaClient.getApplication("car-service").getInstances().get(0);
		String baseUrl = instanceInfo.getHomePageUrl();
		
		String path = "/car/";
		
		//                   localhost:8082/car/1 gibi birşey oluşacak
		String servicePath = baseUrl+path+carId.toString();
		
		ResponseEntity<CarDTO> carDTOresponse =restTemplate.getForEntity(servicePath, CarDTO.class); //burada servicePath'e gidip, CarDTO döndürecek bir mekanizmayı, POSTMAN
															 //kullanması için hazırlıyorum
		
		if(!(carDTOresponse.getStatusCode()==HttpStatus.OK)) {
			throw new ResourceNotFoundException("Car not found with id "+carId);
		}
		
        CarDTO carDTO=carDTOresponse.getBody();
		
		Reservation reservation=new Reservation();
		
		reservation.setCarId(carDTO.getId());
		
		reservation.setPickUpTime(reservationRequest.getPickUpTime());
		reservation.setDropOffTime(reservationRequest.getDropOffTime());
		reservation.setPickUpLocation(reservationRequest.getPickUpLocation());
		reservation.setDropOffLocation(reservationRequest.getDropOffLocation());
		
		
		reservation.setStatus(ReservationStatus.CREATED);
		
		double tp= totalPrice(reservationRequest.getPickUpTime(), reservationRequest.getDropOffTime(),carDTO);
		reservation.setTotalPrice(tp);
		
		reservationRepository.save(reservation);
		
	}
	
	//--------------------get all reservations -------------------------------------------------
	
	public List<ReservationDTO> getAllReservations(){
		List<Reservation> rList = reservationRepository.findAll();
		
		List<ReservationDTO> rDTOList = rList.stream().map(this::mapReservationToDTO).collect(Collectors.toList());
		return rDTOList;
		
		
	}
	
	private ReservationDTO mapReservationToDTO(Reservation reservation) {
		ReservationDTO reservationDTO =modelMapper.map(reservation, ReservationDTO.class);
		return reservationDTO;
	}
	
	
	//calculate total price 
	private Double totalPrice(LocalDateTime pickUpTime, LocalDateTime dropOffTime, CarDTO carDTO) {
		   Long hours=	(new Reservation()).getTotalHours(pickUpTime, dropOffTime);
		   return carDTO.getPricePerHour()*hours;
		}
	

}

package com.example.reservationclient

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.netflix.feign.EnableFeignClients
import org.springframework.cloud.netflix.feign.FeignClient
import org.springframework.cloud.netflix.zuul.EnableZuulProxy
import org.springframework.web.bind.annotation.*

@EnableCircuitBreaker
@EnableFeignClients
@EnableZuulProxy
@EnableDiscoveryClient
@SpringBootApplication
class ReservationClientApplication

fun main(args: Array<String>) {
    SpringApplication.run(ReservationClientApplication::class.java, *args)
}

@FeignClient("reservation-service")
interface ReservationClient {

    @GetMapping("/reservations")
    fun read(): Array<Reservation>
}

data class Reservation(var id: Long? = null, var reservationName: String? = null)

@RestController
class ReservationApiAdapterRestController(val reservationClient: ReservationClient) {

    fun fallback() : List<String?> = emptyList()

    @HystrixCommand(fallbackMethod = "fallback")
    @GetMapping("/reservations/names")
    fun names(): List<String?> = reservationClient.read().map { it.reservationName }
}
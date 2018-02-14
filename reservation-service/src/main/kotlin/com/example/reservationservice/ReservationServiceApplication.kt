package com.example.reservationservice

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@EnableDiscoveryClient
@SpringBootApplication
class ReservationServiceApplication {
    @Bean
    fun initializer(reservationRepository: ReservationRepository) = ApplicationRunner {
        arrayOf("Orlando", "Zuss", "Sier", "Oscar", "Nydia", "Valeria", "Angel", "Luis", "Juan", "Miriam")
                .forEach { name -> reservationRepository.save(Reservation(reservationName = name)) }

        reservationRepository
                .findAll()
                .forEach{ println(it) }
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(ReservationServiceApplication::class.java, *args)
}


interface ReservationRepository : JpaRepository<Reservation, Long>

@Entity
data class Reservation(@Id @GeneratedValue var id: Long? = null, var reservationName: String? = null)

@RestController
class ReservationRestController(val reservationRepository: ReservationRepository) {

    @GetMapping("/reservations")
    fun reservations() = reservationRepository.findAll()

    @GetMapping("/reservations/{id}")
    fun reservationById(@PathVariable("id") id: Long) = reservationRepository.findOne(id)
}

@RestController
@RefreshScope
class MessageProcessor(@Value("\${message}") msg: String) {
    var message: String? = msg

    @GetMapping("/message")
    fun message() = this.message
}

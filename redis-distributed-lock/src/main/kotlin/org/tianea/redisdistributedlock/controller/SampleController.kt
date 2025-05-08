package org.tianea.redisdistributedlock.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.tianea.redisdistributedlock.service.SampleService

@RestController
@RequestMapping("/api")
class SampleController(
    private val sampleService: SampleService
) {

    @GetMapping("/users/{userId}/process")
    fun processUserData(
        @PathVariable userId: String,
        @RequestParam data: String
    ): String {
        return sampleService.processUserData(userId, data)
    }

    @GetMapping("/orders")
    fun processOrder(
        @RequestParam userId: String,
        @RequestParam itemId: String,
        @RequestParam quantity: Int
    ): String {
        return sampleService.processOrder(userId, itemId, quantity)
    }
    
    @GetMapping("/reports/{reportId}")
    fun generateReport(
        @PathVariable reportId: String,
        @RequestParam userId: String
    ): String {
        return sampleService.generateReport(reportId, userId)
    }
    
    @GetMapping("/statistics/{statId}")
    fun viewStatistics(
        @PathVariable statId: String
    ): String {
        return sampleService.viewStatistics(statId)
    }
}

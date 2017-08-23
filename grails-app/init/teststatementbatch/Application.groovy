package teststatementbatch

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import org.springframework.retry.annotation.EnableRetry

@EnableRetry
class Application extends GrailsAutoConfiguration {
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }
}
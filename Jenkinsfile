pipeline {
    agent any
    tools { maven 'Maven3'; jdk 'JDK11' }
    stages {
        stage('Checkout') { steps { checkout scm } }
        stage('Install Playwright Browsers') { steps { sh 'mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args=install' } }
        stage('Run Tests') { steps { sh 'mvn clean test' } }
        stage('Allure Report') { steps { allure includeProperties: false, results: [[path: 'target/allure-results']] } }
    }
}
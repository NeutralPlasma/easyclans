def checksConclusion() {
    def result = currentBuild?.currentResult ?: 'SUCCESS'
    if (result == 'SUCCESS') {
        return 'SUCCESS'
    }
    if (result == 'UNSTABLE') {
        return 'NEUTRAL'
    }
    if (result == 'ABORTED') {
        return 'CANCELED'
    }
    if (result == 'NOT_BUILT') {
        return 'SKIPPED'
    }
    return 'FAILURE'
}

pipeline {
    agent {
        label 'linux'
    }

    options {
        timestamps()
    }

    environment {
        GITHUB_USER = "NeutralPlasma"
        GITHUB_REPO = "EasyClans"
        GITHUB_PUBLIC_REPO = "EasyClans"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Repository Mining') {
            steps {
                withChecks(name: 'EasyClans', includeStage: true) {
                    script {
                        try {
                            // Discover baseline for delta/comparison
                            mineRepository()
                            gitDiffStat()
                        } finally {
                            publishChecks(status: 'COMPLETED', conclusion: checksConclusion())
                        }
                    }
                }
            }
        }

        stage('Build') {
            when {
                anyOf {
                    branch 'main'
                    tag pattern: ".*", comparator: "REGEXP"
                }
            }
            steps {
                withChecks(name: 'EasyClans', includeStage: true) {
                    script {
                        try {
                            sh 'chmod +x ./gradlew'
                            sh './gradlew clean shadowJar'
                        } finally {
                            publishChecks(status: 'COMPLETED', conclusion: checksConclusion())
                        }
                    }
                }
            }
        }

//         stage('Publish API') {
//             when {
//                 tag pattern: ".*", comparator: "REGEXP"
//             }
//             steps {
//                 script {
//                     catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
//                         script {
//                             try {
//                                 sh 'chmod +x ./gradlew'
//                                 sh './gradlew :api:publish'
//                             } finally {
//                                 publishChecks(status: 'COMPLETED', conclusion: checksConclusion())
//                             }
//                         }
//                     }
//                 }
//             }
//         }

        stage('Release to GitHub') {
            when {
                tag pattern: ".*", comparator: "REGEXP"
            }
            steps {
                withChecks(name: 'EasyClans', includeStage: true) {
                    script {
                        try {

                            def tagName = env.TAG_NAME
                            if (!tagName) {
                                tagName = env.GIT_TAG
                            }
                            if (!tagName) {
                                tagName = sh(
                                    returnStdout: true,
                                    script: 'git describe --tags --exact-match 2>/dev/null || true'
                                ).trim()
                            }
                            def commitSha = env.GIT_COMMIT
                            def releaseNotes = ""
                            try {
                                releaseNotes = generateChangelog(currentTag: tagName)
                            } catch (e) {
                                echo "Changelog generation failed: ${e}"
                                releaseNotes = "Automated release for ${tagName}"
                            }

                            def paperJar = sh(
                                returnStdout: true,
                                script: 'ls -t build/libs/*.jar | head -n 1'
                            ).trim()

                            if (!tagName) {
                                error("Tag name not found. Ensure this is a tag build or TAG_NAME/GIT_TAG is set.")
                            }
                            if (!paperJar) {
                                error("No Paper shadow JAR found in build/libs")
                            }

                            uploadToGithub(
                                credentials: 'GITHUB_JENKINS',
                                user: env.GITHUB_USER,
                                repository: env.GITHUB_REPO,
                                tag: tagName,
                                commitSha: commitSha,
                                description: releaseNotes,
                                artifacts: [
                                    [name: "EasyClans-${tagName}.jar", path: paperJar]
                                ]
                            )
                        }finally {
                            publishChecks(status: 'COMPLETED', conclusion: checksConclusion())
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'build/libs/*.jar', allowEmptyArchive: true, fingerprint: true
        }
    }
}

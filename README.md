# Quarkus Lambda Template

Repository template for a Quarkus-based AWS Lambda packaged as a Docker image.

This README explains how to build and test locally, how the Docker build works, CI conventions used in this repo (ECR-check before build), and how the repo can communicate with a separate infra repository to provision resources like ECR.

---

## Quick summary

- Framework: Quarkus
- Artifact: native/JVM AWS Lambda packaged as a container image
- Dockerfile: `Dockerfile` (multi-stage: `build` and `runtime`)
- Checkstyle: `config/checkstyle/checkstyle.xml`
- OpenAPI: `src/main/resources/openapi.yaml`

---

## Prerequisites

- Java jdk 25
- Maven installed
- Docker Desktop running (for building the image)
- AWS CLI configured (for pushing to ECR or invoking Lambda) or appropriate GitHub Actions secrets

---

#!/bin/bash

# Docker Build Script for One-You Application
# Dev 환경 배포용

set -e  # 에러 시 스크립트 중단

echo "🐳 Building One-You Docker Image..."

# 변수 설정
IMAGE_NAME="one-you-app"
TAG="dev-latest"
FULL_IMAGE_NAME="${IMAGE_NAME}:${TAG}"

# 1. 먼저 JOOQ 코드 생성 (로컬 DB 사용)
echo "🔧 Generating JOOQ code..."
./gradlew generateJooq

# 2. Docker 빌드 실행
echo "📦 Building Docker image: ${FULL_IMAGE_NAME}"
docker build -t ${FULL_IMAGE_NAME} .

# 빌드 성공 확인
if [ $? -eq 0 ]; then
    echo "✅ Docker build completed successfully!"
    echo "📋 Image details:"
    docker images | grep ${IMAGE_NAME}
    
    echo ""
    echo "🚀 To run the container:"
    echo "   docker run -p 8080:8080 ${FULL_IMAGE_NAME}"
    echo ""
    echo "🐙 To push to ECR (after AWS login):"
    echo "   docker tag ${FULL_IMAGE_NAME} <ECR_URI>/${IMAGE_NAME}:${TAG}"
    echo "   docker push <ECR_URI>/${IMAGE_NAME}:${TAG}"
else
    echo "❌ Docker build failed!"
    exit 1
fi
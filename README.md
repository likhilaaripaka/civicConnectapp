# CivicConnect - Community Issue Management System

CivicConnect is a comprehensive web application that connects citizens with local authorities to report, track, and resolve community issues efficiently. Built with Spring Boot, PostgreSQL, and modern web technologies.

## Features

### For Citizens
- **User Registration & Login**: Easy signup and authentication
- **Issue Reporting**: Report issues with text, images, and videos
- **Location Tagging**: Tag issues with specific locations
- **Category Filtering**: Organize issues by categories (Roads, Electricity, Water Supply, etc.)
- **Community Support**: Support issues with thumbs up
- **Comments & Discussion**: Engage with community through comments
- **Priority System**: Issues with more support appear first

### For Authorities (Admins)
- **Admin Dashboard**: View and manage issues in their jurisdiction
- **Location-Based Filtering**: See only issues in their area
- **Issue Resolution**: Mark issues as solved
- **Solved Issues Tracking**: Keep track of resolved issues
- **Priority Management**: Handle issues based on community support

## Technology Stack

- **Backend**: Java 17, Spring Boot 3.2.0, Spring Security, Spring Data JPA
- **Database**: PostgreSQL (Neon Cloud Database)
- **Frontend**: HTML5, CSS3, Bootstrap 5, Thymeleaf
- **Build Tool**: Maven
- **Containerization**: Docker, Docker Compose

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- PostgreSQL database (or use Neon Cloud Database)
- Docker (optional, for containerized deployment)

## Installation & Setup

### Option 1: Local Development

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd civicconnect
   ```

2. **Configure Database**
   - Update `src/main/resources/application.properties` with your database credentials
   - For Neon Database, replace the connection string with your actual credentials:
   ```properties
   spring.datasource.url=jdbc:postgresql://your-neon-host/neondb?sslmode=require
   spring.datasource.username=your-username
   spring.datasource.password=your-password
   ```

3. **Run the Application**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Access the Application**
   - Open your browser and go to `http://localhost:8080`

### Option 2: Docker Deployment

1. **Using Docker Compose**
   ```bash
   docker-compose up -d
   ```

2. **Using Docker**
   ```bash
   # Build the image
   docker build -t civicconnect .
   
   # Run the container
   docker run -p 8080:8080 civicconnect
   ```

## Database Configuration

### For Neon Database (Recommended)

1. Create a Neon database account at [neon.tech](https://neon.tech)
2. Create a new database
3. Update the connection details in `application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://your-host.neon.tech/neondb?sslmode=require
   spring.datasource.username=your-username
   spring.datasource.password=your-password
   ```

### For Local PostgreSQL

1. Install PostgreSQL
2. Create a database named `civicconnect`
3. Update connection details in `application.properties`

## Usage

### Citizen Workflow

1. **Register**: Create an account with your details
2. **Login**: Access your citizen dashboard
3. **Report Issues**: Click "New Issue" to report problems
4. **Support Issues**: Use thumbs up to support important issues
5. **Comment**: Engage in discussions about issues
6. **Filter**: Use category filters to find specific issues

### Admin Workflow

1. **Admin Login**: Use admin credentials with location
2. **View Issues**: See all issues in your jurisdiction
3. **Resolve Issues**: Mark issues as solved when addressed
4. **Track Progress**: Monitor solved issues in the dashboard

## Project Structure

```
src/
├── main/
│   ├── java/com/civicconnect/
│   │   ├── controller/          # REST controllers
│   │   ├── model/              # Entity models
│   │   ├── repository/         # Data access layer
│   │   ├── service/            # Business logic
│   │   └── CivicConnectApplication.java
│   └── resources/
│       ├── templates/          # Thymeleaf templates
│       └── application.properties
├── test/                       # Test files
pom.xml                        # Maven configuration
Dockerfile                     # Docker configuration
docker-compose.yml            # Docker Compose setup
README.md                     # This file
```

## API Endpoints

### Public Endpoints
- `GET /` - Home page
- `GET /about` - About page
- `GET /features` - Features page
- `GET /login` - Citizen login
- `GET /admin-login` - Admin login
- `GET /register` - Citizen registration

### Citizen Endpoints
- `GET /citizen-dashboard` - Citizen dashboard
- `GET /issues/category/{category}` - Filter issues by category
- `GET /issue/{id}` - View issue details
- `POST /issue/{id}/comment` - Add comment
- `POST /issue/{id}/support` - Toggle support
- `GET /new-issue` - New issue form
- `POST /new-issue` - Submit new issue

### Admin Endpoints
- `GET /admin-dashboard` - Admin dashboard
- `GET /admin/issue/{id}` - View issue (admin)
- `POST /admin/issue/{id}/solve` - Mark issue as solved

## Configuration

### Application Properties

Key configuration options in `application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://your-host/neondb?sslmode=require
spring.datasource.username=your-username
spring.datasource.password=your-password

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# File Upload
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Server
server.port=8080
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support and questions:
- Create an issue in the repository
- Contact the development team

## Changelog

### Version 1.0.0
- Initial release
- Citizen registration and login
- Issue reporting with media support
- Admin dashboard and issue management
- Comment and support system
- Category-based filtering
- Priority system based on community support

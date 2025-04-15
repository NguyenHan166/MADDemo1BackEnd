## Course DTO

The `CourseDto` object represents a course with the following fields:

### Properties

| Field              | Type                    | Description                                                                 |
|--------------------|-------------------------|-----------------------------------------------------------------------------|
| `id`               | integer (`int64`)        | Unique identifier of the course.                                             |
| `name`             | string                  | The name of the course.                                                     |
| `description`      | string                  | A brief description of the course.                                           |
| `teacher`          | string                  | The name of the course instructor.                                           |
| `timeStart`        | string (`date-time`)     | The start date and time of the course in ISO 8601 format.                   |
| `timeEnd`          | string (`date-time`)     | The end date and time of the course in ISO 8601 format.                     |
| `numberOfLesion`   | integer (`int32`)        | The number of lessons in the course.                                         |
| `addressLearning`  | string                  | The address where the course will take place.                               |
| `repeatTime`       | string                  | The repeat time for the course (e.g., weekly, monthly).                     |
| `state`            | string                  | The current state of the course (e.g., ongoing, pending, finished).         |
| `scheduleLearningList` | array of `ScheduleLearningDto` | A list of schedule details related to this course.                           |

---

## Schedule Learning DTO

The `ScheduleLearningDto` object represents a learning schedule with the following fields:

### Properties

| Field               | Type                    | Description                                                                 |
|---------------------|-------------------------|-----------------------------------------------------------------------------|
| `id`                | integer (`int64`)        | Unique identifier of the schedule.                                          |
| `description`       | string                  | A description of the schedule.                                               |
| `timeStart`         | string (`date-time`)     | The start time of the schedule in ISO 8601 format.                           |
| `timeEnd`           | string (`date-time`)     | The end time of the schedule in ISO 8601 format.                             |
| `teacher`           | string                  | The name of the teacher assigned to this schedule.                           |
| `learningAddresses` | string                  | The address where the lesson will take place.                               |
| `state`             | string                  | The current state of the schedule (e.g., ongoing, pending, completed).      |
| `courseID`          | integer (`int64`)        | The ID of the course associated with this schedule.                          |


## Example JSON Structure

```json
{
  "id": 1,
  "name": "Introduction to Programming",
  "description": "Learn the basics of programming in Python.",
  "teacher": "John Doe",
  "timeStart": "2025-05-01T08:00:00Z",
  "timeEnd": "2025-05-01T10:00:00Z",
  "numberOfLesion": 10,
  "addressLearning": "123 Learning Street, City, Country",
  "repeatTime": "weekly",
  "state": "ongoing",
  "scheduleLearningList": [
    {
      "id": 1,
      "description": "Lesson 1: Introduction to Python",
      "timeStart": "2025-05-01T08:00:00Z",
      "timeEnd": "2025-05-01T09:00:00Z",
      "teacher": "Jane Smith",
      "learningAddresses": "123 Learning Street, City, Country",
      "state": "ongoing",
      "courseID": 1
    },
    {
      "id": 2,
      "description": "Lesson 2: Variables and Data Types",
      "timeStart": "2025-05-08T08:00:00Z",
      "timeEnd": "2025-05-08T09:00:00Z",
      "teacher": "Jane Smith",
      "learningAddresses": "123 Learning Street, City, Country",
      "state": "pending",
      "courseID": 1
    }
  ]
}
```


## API Endpoint

### `GET http://localhost:8080/api/scheduleLearnings/course?courseId=152`

Retrieve List ScheduleLearning of a specific course by its ID.

**Request**

- Param: `courseId`

**Response:**

- Status: `200 OK`
- Body: The List `ScheduleLearningDto` object.


### `GET http://localhost:8080/api/scheduleLearnings/fetch?id=342`

Retrieve details ScheduleLearning  by its ID.

**Request**

- Param: `id`

**Response:**

- Status: `200 OK`
- Body: The List `ScheduleLearningDto` object.

### `GET http://localhost:8080/api/courses/`

Retrieve List Course of User.

**Response:**

- Status: `200 OK`
- Body: The List `CourseDto` object.


### `GET http://localhost:8080/api/courses/fetch?courseId=152`

Retrieve Course Details.

**Request**

- Param: `courseId`

**Response:**

- Status: `200 OK`
- Body: The List `CourseDto` object.

### `POST http://localhost:8080/api/courses/create`

Create Course

**Request Body**

- Body: `CourseDto` Object

**Response:**

- Status: `200 OK`
- Body: The `CourseDto` object.

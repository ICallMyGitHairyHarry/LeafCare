# LeafCare — android-приложение для ухода за растениями
## Описание приложения
### Актуальность
Все больше людей окружают себя комнатными растениями, которые создают уют и улучшают микроклимат.
Однако здоровье растений требует знаний и ухода: полива, освещения, удобрений и пересадки. 
Поэтому есть потребность в удобном, интуитивно понятном и функциональном приложении, объединяющем все инструменты для ухода за растениями.    

**Демонстрация приложения: https://youtu.be/_hfUvDdC7Xg**
### Функционал
1. Учётные записи:    
- Регистрация;
- Авторизация;
- Выход из учётной записи;
2. Управление растениями:    
- Создание;
- Удаление;
- Просмотр списка растений;
- Просмотр конкретного растения;    
- При создании можно добавить: Название, Описание, Фото, Высота;
3. Изучение типов растений (просмотр списка типов);
4. Отслеживание состояния растений с помощью заметок:    
- Создание заметки;
- Просмотр заметки;
- Динамическое обновление высоты растения.
## Разработка приложения
Приложение реализовано в команде:
1. Android-разработчик (я, данный репозиторий)
2. UX/UI-дизайнер
3. Backend-разработчик
4. Разработчик баз данных
5. Менеджер проекта
### Backend и развёртывание
Бэкенд реализован на языке Python с применением фреймворка FastAPI.
Развертка бэкенда осуществляется в двух контейнерах: контейнер для FastAPI, и второй контейнер для базы данных postgresql. В качестве технологии контейнеризации используется Docker compose v2.
Система авторизации построена на основе JWT. Имеется два токена: access_token, и refresh_token.
### Релизация Android-клиента
- Для разработки был выбран язык Kotlin и среда Android Studio
- Приложение построено по архитектуре MVVM (Model - View- ViewModel)
- Используются библиотеки Moshi, Retrofit, Okhttp
- Обеспечена плавность и стабильность приложения
#### Структура проекта
![image](https://github.com/user-attachments/assets/79d953de-ae23-4663-809e-8bf15d37453d)
#### Навигация
Навигация приложения разработана с помощью компонента Jetpack Navigation Component. Было сделано 2 навигационных графа: для авторизации и для главной активности. Граф для главной активности изображён ниже.
![image](https://github.com/user-attachments/assets/552e447a-bbd3-4fff-988e-8add49a43b5d)
#### Взаимодействие с сервером
Для взаимодействия с сервером было реализовано 2 Retrofit-интерфейса: для авторизации и для основного функционала. Токены для авторизации сохраняются с помощью EncryptedSharedPreferences
Токен access_token прикрепляется к каждому запросу с помощью interceptor библиотеки OkHttp
##### AuthApiService.kt
```
private const val BASE_URL =
    "http://10.0.2.2:8000"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

val client = OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor)
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .client(client)
    .baseUrl(BASE_URL)
    .build()

interface AuthApiService {

    @POST("/api/auth/signup")
    suspend fun register(@Body registerRequest: RegisterRequest): AuthResponse

    @POST("/api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): AuthResponse

    @POST("/api/auth/refresh")
    fun refreshTokens(@Body refreshTokenRequest: RefreshTokenRequest): Call<AuthResponse>

}

object AuthApi {
    val retrofitService : AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java) }
}
```
Константа BASE_URL задаёт базовый URL для HTTP-запросов: http://10.0.2.2:8000. Этот адрес используется для работы с сервером через эмулятор Android, где 10.0.2.2 является локальным хостом на машине.  

Для обработки JSON-данных используется библиотека Moshi. Объект Moshi создаётся с помощью Moshi.Builder() и включает адаптер KotlinJsonAdapterFactory, который упрощает работу с Kotlin-классами при сериализации и десериализации JSON.    

Клиент также включает логирование запросов и ответов. Для этого используется HttpLoggingInterceptor с установленным уровнем логгирования BODY, что позволяет записывать тело запросов и ответов в логи. Логгер добавляется в HTTP-клиент, созданный через OkHttpClient.Builder().    

Объект Retrofit, основной инструмент для взаимодействия с API, создаётся с помощью Retrofit.Builder(). В его настройках указывается базовый URL, преобразователь JSON (Moshi) и настроенный HTTP-клиент с логгированием. Это позволяет легко выполнять запросы к серверу.    

Для работы с API определён интерфейс AuthApiService, который описывает три метода. Метод register отправляет POST-запрос на конечную точку /api/auth/signup для регистрации нового пользователя. Метод login выполняет POST-запрос на /api/auth/login для авторизации. Метод refreshTokens отправляет POST-запрос на /api/auth/refresh для обновления токенов. Все методы принимают параметры в виде объектов (RegisterRequest, LoginRequest, RefreshTokenRequest) и возвращают объект AuthResponse.   

Для удобного доступа к API создан объект-синглтон AuthApi, который использует retrofit.create(AuthApiService::class.java) для создания реализации интерфейса AuthApiService. Поле retrofitService предоставляет доступ к методам API. В итоге этот клиент абстрагирует взаимодействие с сервером, предоставляя удобные методы для выполнения сетевых запросов.    
##### PlantsApiService.kt:
```
private const val BASE_URL =
    "http://10.0.2.2:8000"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

interface PlantsApiService {

    @GET("/api/user/plants_get")
    suspend fun getMyPlants(): List<Plant>

    @GET("/api/user/plants_get_types")
    suspend fun getPlantTypes(): List<PlantType>

    @POST("/api/user/plants_create")
    suspend fun createPlant(@Body plantCreateRequest: PlantCreateRequest)

    @POST("/api/user/plants_delete")
    suspend fun deletePlant(@Query("plant_id") plantId: Int)

    @GET("/api/user/plants_get_growth_logs")
    suspend fun getPlantGrowthLogs(@Query("plant_id") plantId: Int): List<PlantNote>

    @POST("/api/user/plants_add_growth_log")
    suspend fun createPlantNote(@Body plantNoteCreateRequest: PlantNoteCreateRequest)

}

object PlantsApi {

    private lateinit var retrofit: Retrofit

    fun init(tokenManager: TokenManager) {

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(TokenInterceptor(tokenManager)) // Attach access token
            .authenticator(TokenAuthenticator(tokenManager)) // Handle token refresh
            .build()

        retrofit = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .baseUrl(BASE_URL)
            .build()
    }

    val retrofitService: PlantsApiService by lazy {
        retrofit.create(PlantsApiService::class.java)
    }

}
```

Интерфейс PlantsApiService описывает методы для взаимодействия с API. Метод getMyPlants отправляет GET-запрос на /api/user/plants_get и возвращает список растений пользователя в виде объектов Plant. Метод getPlantTypes получает список типов растений через GET-запрос к /api/user/plants_get_types. Метод createPlant отправляет POST-запрос на /api/user/plants_create с телом запроса PlantCreateRequest, создавая новое растение. Метод deletePlant удаляет растение через POST-запрос к /api/user/plants_delete, передавая идентификатор растения как параметр запроса. Метод getPlantGrowthLogs отправляет GET-запрос на /api/user/plants_get_growth_logs для получения записей о росте конкретного растения, используя его идентификатор. Метод createPlantNote добавляет новую запись о росте растения через POST-запрос на /api/user/plants_add_growth_log, передавая данные в теле запроса (PlantNoteCreateRequest).

Объект PlantsApi является точкой входа для работы с этим API. В нём определён метод init, который инициализирует объект Retrofit. Этот метод принимает в качестве параметра tokenManager — менеджер токенов авторизации. В методе создаётся HTTP-клиент, к которому добавляются несколько компонентов: HttpLoggingInterceptor для логгирования запросов и ответов, TokenInterceptor для добавления токена доступа к каждому запросу, и TokenAuthenticator для автоматического обновления токена при его истечении. После этого объект Retrofit настраивается с базовым URL, клиентом и преобразователем JSON (Moshi).

Поле retrofitService предоставляет доступ к методам интерфейса PlantsApiService. Оно инициализируется лениво и создаёт реализацию интерфейса через retrofit.create(PlantsApiService::class.java). Таким образом, этот API-клиент обеспечивает удобный способ взаимодействия с сервером, включая обработку токенов авторизации и расширяемую конфигурацию HTTP-запросов.

##### TokenAuthenticator.kt:
```
class TokenAuthenticator (
    private val tokenManager: TokenManager
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        synchronized(this) {
            // Check if the request has already been retried
            if (response.request.header("Authorization") == null) {
                return null
            }

            // Get the current refresh token
            val refreshToken = tokenManager.getRefreshToken()

            // Refresh the token using the API
            val newAccessToken = try {
                // refreshToken can't be null because there is check in LauncherActivity
                val tokenResponse = AuthApi.retrofitService.refreshTokens(RefreshTokenRequest(refreshToken!!)).execute()
                if (tokenResponse.isSuccessful) {
                    val body = tokenResponse.body()
                    if (body != null) {
                        // Save both tokens
                        tokenManager.saveTokens(body.accessToken, body.refreshToken)
                        body.accessToken // Return the accessToken for retry logic
                    } else {
                        null // Handle empty response body
                    }
                } else {
                    null // Refresh failed
                }
            } catch (e: Exception) {
                null // Handle errors during token refresh
            }

            // If the token is null, return null (this will end the retry attempt)
            if (newAccessToken.isNullOrEmpty()) {
                return null
            }

            // Retry the failed request with the new token
            return response.request.newBuilder()
                .header("Authorization", "Bearer $newAccessToken")
                .build()
        }
    }
}
```
Класс TokenAuthenticator отвечает за автоматическое обновление токенов доступа (access token) в случае их истечения. Класс используется в сетевых запросах, отправляемых с помощью OkHttp, и позволяет избежать повторного выполнения запросов вручную, если сервер вернёт ошибку авторизации.

Класс TokenAuthenticator реализует интерфейс Authenticator, предоставляемый OkHttp. Конструктор принимает экземпляр TokenManager, который используется для управления токенами доступа и обновления (refresh token). Основной метод authenticate вызывается OkHttp всякий раз, когда сервер возвращает статус 401 (Unauthorized), сигнализируя об истечении или недействительности токена доступа.

В методе authenticate сначала проверяется, не была ли запрос уже повторён ранее, чтобы избежать бесконечного цикла запросов. Это делается путём проверки наличия заголовка Authorization в запросе. Если заголовок отсутствует, дальнейшие действия прекращаются, и запрос не повторяется.

Далее из TokenManager извлекается текущий refresh token. С помощью этого токена отправляется запрос на сервер для его обновления. Используется API-клиент AuthApi, метод refreshTokens, который принимает RefreshTokenRequest с текущим refresh token. Запрос выполняется синхронно с использованием метода execute().

Если запрос успешен и сервер возвращает новый access token и refresh token, они сохраняются в TokenManager. Новый access token используется для повторного выполнения исходного запроса, который изначально завершился ошибкой авторизации. Для этого создаётся новый запрос на основе старого с обновлённым заголовком Authorization, содержащим новый access token.

Если обновление токена завершилось неудачей (например, из-за ошибки соединения или отказа сервера), метод возвращает null. Это указывает OkHttp прекратить повторные попытки и оставить ошибку авторизации необработанной.

Таким образом, TokenAuthenticator является ключевым компонентом, который обеспечивает плавную работу приложения при истечении срока действия токенов, автоматически обрабатывая их обновление и повторный запуск запросов без вмешательства пользователя.

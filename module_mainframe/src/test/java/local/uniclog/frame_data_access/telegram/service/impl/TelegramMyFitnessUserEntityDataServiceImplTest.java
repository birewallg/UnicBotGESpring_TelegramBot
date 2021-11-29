package local.uniclog.frame_data_access.telegram.service.impl;

import local.uniclog.frame_data_access.DataServiceTestConfiguration;
import local.uniclog.frame_data_access.telegram.entity.TelegramMyFitnessUserEntity;
import local.uniclog.frame_data_access.telegram.service.TelegramMyFitnessUserEntityDataService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@DataJpaTest
@ContextConfiguration(classes = DataServiceTestConfiguration.class)
class TelegramMyFitnessUserEntityDataServiceImplTest {
    @Autowired
    @Qualifier("beanTelegramMyFitnessUserEntityDataServiceTest")
    private TelegramMyFitnessUserEntityDataService entityDataService;

    private TelegramMyFitnessUserEntity entity;

    private final Long telegramId = 123L;
    private final String userName = "Name";
    private final Boolean subscriber = true;
    private final Integer waterCount = 0;

    @BeforeEach
    void setUp() {
        entity = new TelegramMyFitnessUserEntity();
        entity.setUserTelegramId(telegramId);
        entity.setUserName(userName);
        entity.setSubscriber(subscriber);
        entity.setWaterCount(waterCount);
        entityDataService.save(entity);
    }

    @Test
    void setTelegramTORGUserRepository() {
        assertNotNull(entityDataService);
        assertNotNull(entityDataService.findByUserTelegramId(telegramId));
    }

    @ParameterizedTest
    @CsvSource({"123, Name1, true, 11", "456, , false, "})
    void save(ArgumentsAccessor arguments) {
        assertEquals(entity, entityDataService.findByUserTelegramId(telegramId));
        TelegramMyFitnessUserEntity temp = new TelegramMyFitnessUserEntity();
        temp.setUserTelegramId(arguments.getLong(0));
        temp.setUserName(arguments.getString(1));
        temp.setSubscriber(arguments.getBoolean(2));
        temp.setWaterCount(arguments.getInteger(3));
        entityDataService.save(temp);
        TelegramMyFitnessUserEntity check = entityDataService.findByUserTelegramId(arguments.getLong(0));

        assertAll("User properties",
                () -> assertEquals(temp, check),
                () -> {
                    if (Objects.equals(arguments.getLong(0), telegramId))
                        assertEquals(1, entityDataService.findAll().size());
                    else assertEquals(2, entityDataService.findAll().size());
                }
        );
    }

    @Test
    void update() {
        assertEquals(entity, entityDataService.findByUserTelegramId(telegramId));

        TelegramMyFitnessUserEntity oldEntity = entityDataService.findByUserTelegramId(telegramId);
        oldEntity.setWaterCount(11);
        entityDataService.update(oldEntity);
        TelegramMyFitnessUserEntity newEntity = entityDataService.findByUserTelegramId(telegramId);
        assertAll("User properties",
                () -> assertEquals(entity.getId(), newEntity.getId()),
                () -> assertEquals(userName, newEntity.getUserName()),
                () -> assertEquals(subscriber, newEntity.getSubscriber()),
                () -> assertNotEquals(waterCount, newEntity.getWaterCount())
        );

        TelegramMyFitnessUserEntity temp = new TelegramMyFitnessUserEntity();
        temp.setUserTelegramId(1234567890L);
        entityDataService.update(temp);
        assertNotNull(entityDataService.findByUserTelegramId(1234567890L));
    }

    @Test
    void findByUserTelegramId() {
        TelegramMyFitnessUserEntity newEntity = entityDataService.findByUserTelegramId(telegramId);
        assertEquals(entity, newEntity);
        newEntity = entityDataService.findByUserTelegramId(telegramId+1);
        assertNull(newEntity);
    }

    @ParameterizedTest
    @CsvSource({
            "123, false, 456, false, 0",
            "123, true, , , 1",
            "123, true, 456, false, 1",
            "123, true, 456, true, 2"})
    void findAllSubscribers(ArgumentsAccessor arguments) {
        assertEquals(1, entityDataService.findAllSubscribers().size());

        TelegramMyFitnessUserEntity entityTestSub1 = new TelegramMyFitnessUserEntity();
        entityTestSub1.setUserTelegramId(arguments.getLong(0));
        entityTestSub1.setSubscriber(arguments.getBoolean(1));
        entityDataService.save(entityTestSub1);
        TelegramMyFitnessUserEntity entityTestSub2 = new TelegramMyFitnessUserEntity();
        entityTestSub2.setUserTelegramId(arguments.getLong(2));
        entityTestSub2.setSubscriber(arguments.getBoolean(3));
        entityDataService.save(entityTestSub2);
        assertEquals(arguments.getInteger(4), entityDataService.findAllSubscribers().size());
    }

    @Test
    void findAll() {
        assertEquals(1, entityDataService.findAll().size());
        TelegramMyFitnessUserEntity entityTestSub2 = new TelegramMyFitnessUserEntity();
        entityTestSub2.setUserTelegramId(456L);
        entityDataService.save(entityTestSub2);
        assertEquals(2, entityDataService.findAll().size());
    }

    @Test
    void deleteAllByUserTelegramId() {
        TelegramMyFitnessUserEntity user  = new TelegramMyFitnessUserEntity();
        user.setUserTelegramId(456L);
        entityDataService.save(user);
        TelegramMyFitnessUserEntity delUser = entityDataService.findByUserTelegramId(user.getUserTelegramId());
        List<TelegramMyFitnessUserEntity> deleted = entityDataService.deleteAllByUserTelegramId(delUser.getUserTelegramId());
        List<TelegramMyFitnessUserEntity> notFoundEntity = entityDataService.deleteAllByUserTelegramId(delUser.getUserTelegramId());
        assertAll("Test case",
                () -> assertNotNull(deleted),
                () -> assertEquals(1, deleted.size()),
                () -> assertNull(notFoundEntity)
        );
    }
}
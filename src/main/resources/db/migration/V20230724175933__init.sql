create table alert_keyword
(
    id      int UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    keyword varchar(255) NOT NULL,
    guid    char(36)     NOT NULL
);

create table device_group
(
    id   int UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name varchar(255) NOT NULL,
    guid varchar(36)  NOT NULL
);

create table message_device
(
    id              int UNSIGNED                                  NOT NULL AUTO_INCREMENT PRIMARY KEY,
    device_name     varchar(255)                                  NOT NULL,
    device_type     enum ('DESKTOP', 'MOBILE', 'OTHER', 'TABLET') NOT NULL,
    email           varchar(255),
    guid            char(36)                                      NOT NULL,
    device_group_id int UNSIGNED,
    constraint fk_device_group foreign key (device_group_id) references device_group (id)
);

create table exchange_message
(
    id        int UNSIGNED                       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    content   varchar(255),
    type      enum ('BROADCAST', 'GROUP', 'P2P') NOT NULL,
    timestamp datetime                           NOT NULL,
    guid      varchar(36)                        NOT NULL,
    sender_id int UNSIGNED                   NOT NULL,
    constraint fk_sender foreign key (sender_id) references message_device (id)
);

create table message_keywords
(
    message_id int UNSIGNED NOT NULL,
    keyword_id int UNSIGNED NOT NULL,
    constraint fk_keyword foreign key (keyword_id) references alert_keyword (id),
    constraint fk_message foreign key (message_id) references exchange_message (id)
);

create table message_receivers
(
    message_id  int UNSIGNED NOT NULL,
    receiver_id int UNSIGNED NOT NULL,
    constraint fk_receiver foreign key (receiver_id) references message_device (id),
    constraint fk_message_rv foreign key (message_id) references exchange_message (id)
);

create table if not exists chat_messages (
  id bigint primary key auto_increment,
  conversation_id bigint not null,
  sender_id bigint not null,
  receiver_id bigint not null,
  message varchar(2000) not null,
  is_read bit not null,
  created_at datetime(6) not null
);

create index if not exists idx_chat_conv_created
  on chat_messages (conversation_id, created_at);

create index if not exists idx_chat_receiver_read
  on chat_messages (receiver_id, is_read);

create index if not exists idx_chat_receiver_conv_read
  on chat_messages (receiver_id, conversation_id, is_read);

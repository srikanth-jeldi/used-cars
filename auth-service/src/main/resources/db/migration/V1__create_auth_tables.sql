create table if not exists users (
  id bigint primary key auto_increment,
  full_name varchar(255) not null,
  email varchar(255) not null,
  phone varchar(50) not null,
  password varchar(255) not null,
  enabled bit not null,
  locked bit not null,
  created_at timestamp not null
);

create table if not exists user_roles (
  user_id bigint not null,
  roles varchar(50) not null,
  constraint fk_user_roles_user
    foreign key (user_id) references users(id)
    on delete cascade
);

create table if not exists otp_tokens (
  id bigint primary key auto_increment,
  otp_code varchar(20) not null,
  identifier varchar(255) not null,
  expires_at timestamp not null,
  used bit not null,
  type varchar(30) not null
);

-- important indexes for performance + uniqueness
create unique index if not exists ux_users_email on users(email);
create unique index if not exists ux_users_phone on users(phone);

create index if not exists idx_user_roles_user on user_roles(user_id);

create index if not exists idx_otp_identifier_type_used on otp_tokens(identifier, type, used);
create index if not exists idx_otp_expires_at on otp_tokens(expires_at);

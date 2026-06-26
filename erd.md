```dbml
// Database Markup Language for dbdiagram.io

Table roles {
  id bigint [primary key, increment]
  name varchar [not null, unique]
  description varchar
}

Table users {
  id bigint [primary key, increment]
  username varchar [not null, unique]
  password varchar [not null]
  phone_number varchar [not null, unique]
  email varchar [not null, unique]
  is_active boolean [not null, default: true]
  is_kyc boolean [not null, default: false]
  created_at timestamp [not null]
  role_id bigint [not null]
}

Table kyc_profiles {
  id bigint [primary key, increment]
  id_number varchar [not null, unique]
  full_name varchar [not null]
  dob date [not null]
  sex varchar [not null]
  address varchar [not null]
  id_card_front_url varchar [not null]
  status varchar [not null, default: 'PENDING']
  verified_at timestamp
  created_at timestamp [not null]
  user_id bigint [not null, unique]
}

Table accounts {
  id bigint [primary key, increment]
  account_number varchar [not null, unique]
  balance decimal [not null, default: 0]
  currency varchar [not null, default: 'VND']
  transaction_pin varchar [not null]
  active boolean [not null, default: true]
  updated_at timestamp [not null]
  created_at timestamp [not null]
  user_id bigint [not null]
}

Table transactions {
  id bigint [primary key, increment]
  transaction_code varchar [not null, unique]
  amount decimal [not null]
  description varchar
  status varchar [not null]
  created_at timestamp [not null]
  from_account_id bigint
  to_account_id bigint
}

Table refresh_tokens {
  id bigint [primary key, increment]
  token varchar [not null, unique]
  expiry_date timestamp [not null]
  revoked boolean [not null, default: false]
  created_at timestamp [not null]
  user_id bigint [not null, unique]
}

Table token_blacklists {
  id bigint [primary key, increment]
  access_token varchar(1000) [not null, unique]
  expiry_at timestamp [not null]
  blacklisted_at timestamp [not null]
  created_at timestamp [not null]
}

Ref: users.role_id > roles.id
Ref: kyc_profiles.user_id - users.id
Ref: accounts.user_id > users.id
Ref: transactions.from_account_id > accounts.id
Ref: transactions.to_account_id > accounts.id
Ref: refresh_tokens.user_id - users.id
```

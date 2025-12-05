insert into users (username, password, enabled)
values ('user', '$2a$10$6iWHsWY.Y/BihrMGD/NyYOvu04W7Z0guKtTCSjQqLZCsRjtBZ3LAm', true);

insert into users (username, password, enabled)
values ('admin', '$2a$10$igNaHcGx4oUD3.Jfuj11Auk4jEI6ODOmW.9xssiqbnyxp6dM9F7ne', true);

insert into users(username, password, enabled)
values ('manager', '$2a$10$5GiYGueziQ.hjxPVdrR0HOMsY3k4H8KMDpp2CZ.bCRCcixFJhQFBa', true);

insert into authorities (username, authority)
values ('user', 'ROLE_USER');

insert into authorities (username, authority)
values ('admin', 'ROLE_ADMIN');

insert into authorities(username, authority)
values ('manager', 'ROLE_MANAGER');
drop database if exists wtr;
create database wtr;
use wtr;

create table games (
	game smallint not null,
	friends smallint not null,
	people smallint not null,
	max_score int not null,
	primary key (game));

create table players (
	game smallint not null,
	player enum ('g0','g1','g2','g3','g4','g5','g6','g7','g8','g9') not null,
	soulmate_met enum ('yes','no'),
	instance smallint not null,
	score smallint not null,
	primary key (game, player, instance),
	foreign key (game) references games (game));

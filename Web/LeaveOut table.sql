Create database leaveout;
use leaveout;
SET FOREIGN_KEY_CHECKS=0;
/* 댓글 테이블 */
Create table comment (
  comm_num int primary key not null auto_increment,
  content_num int not null,
  user_num int not null,
  rec_cnt int default 0,
  reg_time datetime,
  files varchar(60),
  constraint fk_comment_content_num 
	foreign key(content_num) references Content(content_num) on delete cascade,
  constraint fk_comment_user_num
        foreign key(user_num) references User(user_num) on delete cascade
);

/* 게시글 */
Create table Content (
content_num int primary key not null auto_increment,
user_num int not null,
view_cnt int default 0,
rec_cnt int default 0,
reg_time datetime,
visibility int default 1,
fence boolean default false,
loc_x double,
loc_y double,
address varchar(30),
files varchar(60),
constraint fk_content_user_num
	foreign key(user_num) references User(user_num)
on delete cascade
on update cascade

);

/* 유저 */
Create table User(
user_num int primary key not null auto_increment,
token_num varchar(60),
id varchar(20),
password varchar(20),
name varchar(30),
email varchar(20),
phone_num varchar(15),
profile varchar(200)

);

/* 체크 */
Create table Checks(
check_num int primary key not null auto_increment,
user_num int not null,
chk_x double,
chk_y double,
expare_date date,
constraint fk_checks_user_num 
	foreign key(user_num) references User(user_num)
	on delete cascade
on update cascade
);

/* 친구 */
Create table Friend(
friend_num int not null,
user_num int not null,
primary key(friend_num,user_num),
constraint fk_friend_user_num 
	foreign key(user_num) references User(user_num) on delete cascade,
	constraint fk_friend_friend_num 
	foreign key(friend_num) references User(user_num)
	on delete cascade
on update cascade
);

/* 태그 */
Create table Tagged(
friend_num int not null,
user_num int not null,
content_num int not null,
constraint fk_Tagged_friend
	foreign key(friend_num,user_num) references Friend(friend_num,user_num) on delete cascade,
constraint fk_Tagged_content_num 
	foreign key(content_num) references Content(content_num)
	on delete cascade
on update cascade
);

/* 카테고리 */
Create table Category(
cate_seq int primary key not null auto_increment,
user_num int not null,
cate_text varchar(10),
constraint fk_category_user_num 
	foreign key(user_num) references User(user_num)
	on delete cascade
on update cascade
);

/* 카테고리 데이터 */
CREATE TABLE `cate_data` (
	`cate_seq` INT(11) NOT NULL,
	`cate_data_text` VARCHAR(50) NOT NULL,
	`content_num` INT(11) NOT NULL,
	INDEX `cate_seq` (`cate_seq`),
	CONSTRAINT `cate_data_ibfk_1` FOREIGN KEY (`cate_seq`) REFERENCES `category` (`cate_seq`) ON UPDATE CASCADE ON DELETE CASCADE
);

/* 댓글에 댓글 데이터 */
Create table recomment (
  recomm_num int primary key not null auto_increment,
  comm_num int not null,  
  content_num int not null,
  recomm_content varchar(60),
  user_num int not null,
  reg_time datetime,
  foreign key(content_num) references comment(content_num) 
  on delete cascade
  on update cascade,
  foreign key(comm_num) references comment(comm_num) 
  on delete cascade
  on update cascade
);

/* 신고 테이블 */
Create table Declaration (
	content_num int not null,
	user_num int not null,
	decl_text varchar(100),
	constraint fk_declaration_content_num
	foreign key(content_num) references Content(content_num) 
	on delete cascade,
	constraint fk_declaration_user_num
	foreign key(user_num) references User(user_num)
	on delete cascade
);

/* 알림 테이블 */
Create table FCM (
	user_num int not null,
	token varchar(200),
	constraint fk_FCM_user_num
	foreign key(user_num) references User(user_num)
	on delete cascade
);






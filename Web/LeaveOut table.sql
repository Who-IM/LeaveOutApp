Create database leaveout;
use leaveout;
SET FOREIGN_KEY_CHECKS=0;
/* 댓글 테이블 */
Create table comment (
  comm_num int primary key not null auto_increment,
  content_num int not null,
  user_name varchar(15),
  rec_cnt int,
  reg_time date,
  constraint fk_comment_content_num 
	foreign key(content_num) references Content(content_num)
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
);

/* 유저 */
Create table User(
user_num int primary key not null auto_increment,
token_num varchar(60),
id varchar(20),
password varchar(20),
name varchar(15),
email varchar(20),
phone_num varchar(15),
profile varchar(60)
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
);

/* 친구 */
Create table Friend(
friend_num int not null,
user_num int not null,
primary key(friend_num,user_num),
constraint fk_friend_user_num 
	foreign key(user_num) references User(user_num),
	constraint fk_friend_friend_num 
	foreign key(friend_num) references User(user_num)
);

/* 태그 */
Create table Tagged(
friend_num int not null,
user_num int not null,
content_num int not null,
constraint fk_Tagged_friend
	foreign key(friend_num,user_num) references Friend(friend_num,user_num),
constraint fk_Tagged_content_num 
	foreign key(content_num) references Content(content_num)
);

/* 카테고리 */
Create table Category(
cate_seq int primary key not null auto_increment,
user_num int not null,
cate_text varchar(10),
constraint fk_category_user_num 
	foreign key(user_num) references User(user_num)
);



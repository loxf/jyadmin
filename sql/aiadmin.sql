/*==============================================================*/
/* Database name:  jingyi                                       */
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2017/10/17 22:15:04                          */
/*==============================================================*/


/*==============================================================*/
/* Table: offer_catalog                                         */
/*==============================================================*/
drop table tb_offer_catalog ;
CREATE TABLE `tb_offer_catalog` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `catalog_id` varchar(64) NOT NULL,
  `catalog_name` varchar(255) NOT NULL COMMENT '分类名称',
  `pic` varchar(500) DEFAULT NULL COMMENT '图片',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*==============================================================*/
/* Table: tb_account                                            */
/*==============================================================*/
create table tb_account
(
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   cust_id              varchar(64),
   balance              decimal,
   bp                   decimal,
   is_lock              int(2),
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*==============================================================*/
/* Table: tb_account_detail                                     */
/*==============================================================*/
create table tb_account_detail
(
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   cust_id              varchar(64),
   order_id             varchar(64) comment '订单ID(选填)',
   detail_name          varchar(400) comment '明细名称',
   type                 int(2) comment '类型：1 收入 3 支出',
   balance              decimal comment '余额',
   change_balance       decimal comment '余额改变',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*==============================================================*/
/* Table: tb_active                                             */
/*==============================================================*/
create table tb_active
(
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   active_id            varchar(64) comment '活动ID',
   active_name          varchar(200) comment '活动名称',
   price                decimal comment '活动费用',
   active_privi         varchar(500),
   addr                 varchar(200) comment '活动地点',
   active_start_time    timestamp NULL DEFAULT NULL comment '活动时间起',
   active_end_time      timestamp NULL DEFAULT NULL comment '活动时间止',
   html_id              varchar(64) comment '活动详情富文本',
   status               int(2) comment '1：发布 0：未发布',
   is_deleted           int(2) comment '1:删除 0:默认',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*==============================================================*/
/* Table: tb_active_cust_list                                   */
/*==============================================================*/
create table tb_active_cust_list
(
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   cust_id              varchar(0),
   phone                varchar(30) comment '联系电话',
   name                 varchar(200) comment '联系人',
   active_id            varchar(64) comment '活动ID',
   active_name          varchar(200) comment '活动名称',
   status               int(2) comment '1：已付款，3，已签到',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*==============================================================*/
/* Table: tb_admin                                              */
/*==============================================================*/
create table tb_admin
(
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   user_name            varchar(64),
   password             varchar(64),
   real_name            varchar(100),
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*==============================================================*/
/* Table: tb_agent_info                                         */
/*==============================================================*/
create table tb_agent_info
(
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   cust_id              varchar(64),
   status               int(2) comment '0：申请，1：通过，2：不通过',
   type                 int(2) comment '1：代理商 2：合伙人 3：分公司',
   eff_date             varchar(40),
   exp_date             varchar(40),
   total_free_nbr       int(6) comment '总免费名额',
   use_nbr              int(6) comment '已用名额',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*==============================================================*/
/* Table: tb_config                                             */
/*==============================================================*/
create table tb_config
(
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   config_code          varchar(50) comment '配置CODE',
   config_name          varchar(50) comment '配置名称',
   config_value         varchar(500) comment '值',
   type                 varchar(20) comment 'TEXT：文本 NUMBER:数字，JPG：图片，MP4：视频，URL：链接',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*==============================================================*/
/* Table: tb_cust                                               */
/*==============================================================*/
create table tb_cust
(
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   cust_id              varchar(64) not null comment '客户ID',
   nick_name            varchar(120) comment '昵称',
   user_level           varchar(15) comment 'NONE, VIP, SVIP',
   phone                varchar(30) comment '手机号',
   email                varchar(100) comment '邮箱',
   is_chinese           int(2) comment '1:中国，2:海外用户',
   recommend             varchar(64) comment '推荐人',
   is_agent             int(2) comment '0：否，1：代理商，2：合伙人，3：分公司',
   address              varchar(200),
   openid            varchar(64),
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*==============================================================*/
/* Table: tb_cust_bank                                          */
/*==============================================================*/
create table tb_cust_bank
(
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   cust_id              varchar(64),
   bank                 varchar(100) comment '银行',
   bank_no              varchar(100) comment '卡号',
   address              varchar(200) comment '省份 城市',
   zh_name              varchar(100) comment '支行名称',
   user_name            varchar(60) comment '持卡人',
   phone                varchar(30) comment '手机',
   status               int(2) comment '状态 1 绑定 0 解绑',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*==============================================================*/
/* Table: tb_cust_bp_detail                                     */
/*==============================================================*/
create table tb_cust_bp_detail
(
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   cust_id              varchar(64) comment 'varchar(64)',
   detail_name          varchar(400) comment '明细名称',
   type                 int(2) comment '类型：1 收入 3 支出',
   bp_balance           decimal comment '积分余额',
   change_balance       int(8) comment '积分改变',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*==============================================================*/
/* Table: tb_cust_cash                                          */
/*==============================================================*/
create table tb_cust_cash
(
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   cust_id              varchar(64),
   balance              decimal comment '提现金额',
   type                 int(2) comment '1 微信， 2 银行卡',
   obj_id               varchar(64) comment '微信/银行卡ID',
   status               int(2) comment '状态：1：未打款 3：已打款',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*==============================================================*/
/* Table: tb_html_info                                          */
/*==============================================================*/
create table tb_html_info
(
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   html_id              varchar(64) comment '富文本ID',
   html_url             varchar(400) comment '富文本路径',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*==============================================================*/
/* Table: tb_offer                                              */
/*==============================================================*/
create table tb_offer
(
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   offer_id             varchar(64) comment '商品ID',
   offer_name           varchar(200) comment '商品名称',
   catalog_id           varchar(64) comment '商品分类',
   offer_type           varchar(20) comment '商品类型 服务类型：VIP,  课程：CLASS, 套餐：OFFER',
   sale_money           decimal comment '售价',
   discount             decimal comment '折扣',
   main_media           varchar(200) comment '主媒体，不同类型对应的是不同的东西',
   buy_privi            varchar(100) comment '购买权限JSON，含级别，价格',
   meta_data            varchar(2000) comment '元数据',
   html_id              varchar(200) comment '富文本ID',
   status               int(2) comment '是否上架 1:上架 0:下架（默认）',
   is_deleted           int(2) comment '1:删除 0:默认',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*==============================================================*/
/* Table: tb_offer_rel                                          */
/*==============================================================*/
create table tb_offer_rel
(
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   offer_id             varchar(64) comment '主商品ID',
   rel_id               varchar(64) comment '关联商品ID',
   rel_type             varchar(40) comment '关系类型：OFFER 套餐，PARENT主子关系',
   sort              int(4) comment '排序',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*==============================================================*/
/* Table: tb_order                                              */
/*==============================================================*/
create table tb_order
(
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   order_id             varchar(64),
   order_name           varchar(200),
   order_type           int(2) comment '1 商品 3 VIP  5 活动 ',
   order_money          decimal comment '订单金额',
   bp                   decimal comment '消耗积分',
   total_money          decimal comment '总金额',
   discount             decimal comment '折扣',
   obj_id               varchar(64) comment '订购对象',
   status               int(2) comment '1：待支付 3：已支付 -3：支付失败 ',
   pay_type             int(2) comment '支付方式 1：微信支付 3：余额支付',
   cust_id              varchar(64),
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*==============================================================*/
/* Table: tb_purchased_vedio                                    */
/*==============================================================*/
create table tb_purchased_vedio
(
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   cust_id              varchar(64) comment '客户',
   nick_name            varchar(200) comment '客户昵称',
   vedio_id             varchar(64) comment '视频ID',
   vedio_name           varchar(64) comment '视频名称',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*==============================================================*/
/* Table: tb_share                                              */
/*==============================================================*/
create table tb_share
(
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   cust_id              varchar(64),
   type                 varchar(20) comment '分享类型：VIDEO/ACTIVE/RECOMMEND',
   share_obj            varchar(64) comment '分享对象',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*==============================================================*/
/* Table: tb_watch_record                                       */
/*==============================================================*/
create table tb_watch_record
(
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   cust_id              varchar(64),
   video_id             varchar(64),
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*==============================================================*/
/* Table: 视频配置表                                                 */
/*==============================================================*/
create table tb_video_config
(
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   video_id             varchar(64) comment '视频ID',
   video_name           varchar(64) comment '视频名称',
   video_url            varchar(400) comment '视频URL',
   status               varchar(8) comment '视频状态',
   video_out_id         varchar(60) comment '外部视频ID',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*==============================================================*/
/* Table: tb_vip_info                                           */
/*==============================================================*/
create table tb_vip_info
(
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   real_name            varchar(255),
   cust_id              varchar(64),
   status               int(2) comment '1：生效，0：失效',
   eff_date             timestamp NULL DEFAULT NULL,
   exp_date             timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

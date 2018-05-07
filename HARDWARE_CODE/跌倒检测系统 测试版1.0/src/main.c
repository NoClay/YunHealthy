/***************************************************************
跌倒检测系统1.0测试版

说明：
gsm模块手动设定为关闭自动设置波特率，手动设置波特率为9600

待完善功能：
蜂鸣器驱动
gsm休眠及唤醒
gps休眠及唤醒
按键功能全部重写

短信功能一定要完整测试一遍
短信功能一定要完整测试一遍
短信功能一定要完整测试一遍

接口说明：
PA2 USART2 TX
PA3 USART2 RX

PA4 GPS使能 低电平休眠
PA5 GSM使能 低电平唤醒

PA9 USART1 TX
PA10 USART1 RX

PA13 SWCLK
PA14 SWDIO

PB0 mpu6050的SDA
PB1 mpu6050的SCL

PB6 板载24C02的SCL
PB7 板载24C02的SDA

PB10 USART3 TX
PB11 USART3 RX

PB13 按键
PB15 蜂鸣器

PD2 指示灯

测试硬件：
led y
蜂鸣器 y
按键 y
usart1 相对STM32发送正常，接收未测试
usart2 相对STM32接收正常，发送未测试也不必测试
usart3 相对STM32发送正常，接收未测试
mpu6050 y
gsm 可工作，经常启动失败
gps 可工作，未定位
gsm使能 可休眠，唤醒未测试
gps使能 y

目前存在的问题
6050读不出 已解决
gsm模块在启动、发送短信、接打电话时会给mpu6050带来巨大干扰！！！！！
gsm模块在启动、发送短信、接打电话时会给mpu6050带来巨大干扰！！！！！
gsm模块在启动、发送短信、接打电话时会给mpu6050带来巨大干扰！！！！！

应当等待收到SMS Ready\r\n再启动mpu6050

gsm模块启动成功后，仍会给6050带来轻微干扰，具体表现为6050波形有毛刺

gps代码有改动，可能会出问题！！！！！！！！！！！！

按键延时可能需要修改，加了其他函数，while跑一圈的时间变长了

***************************************************************/

#include "sys.h"
#include "delay.h"
#include "key.h"
#include "led.h"
#include "usart.h"
#include "usart2.h"
#include "usart3.h"
#include "gsm.h"
#include "gps.h"
#include "mpu6050.h"
#include "falldown.h"
#include "buzzer.h"
#include "io.h"
#include "STMIIC.h"
#include <math.h>


void SendArray(unsigned char *ArrayName, unsigned short int length);
void initialize(void);


int main(void)
{
//	short int a,b,c;
	unsigned char i, s = 0;
//	
//	Stm32_Clock_Init(9);//倍频设置为2，即主频16MHz
//	delay_init(72);
//	
//	//只有串口1是全速，其它串口的速度都是串口1的一半
//	
//	uart_init(72, 9600);//用于GSM模块
//	USART2_Init(36, 9600);//用于GPS模块
//	USART3_Init(36, 9600);//用于测试
//	
//	
//	Stm32_Clock_Init(2);//倍频设置为2，即主频16MHz
//	delay_init(16);
//	
//	//只有串口1是全速，其它串口的速度都是串口1的一半
//	uart_init(16, 9600);//用于GSM模块
//	USART2_Init(8, 9600);//用于GPS模块
//	USART3_Init(8, 9600);//用于测试
//	
//	IICinit();
//	
//	initmpu6050();
//	
//	//转换电话号码的格式
//	ChangeNumber(GSM_AN, AddresseeNumber);
//	ChangeNumber(GSM_SSCN, ServiceCenterNumber);
//	
//	//发送数据前读取USART1->SR，避免硬件复位后串口发送数据第一字节丢失的情况发生
//	i = USART1->SR;
//	
//	LED_Init();
//	LED = 1;//熄灭led
//	
//	Buzzer_Init();
//	BUZZER = 1;//关闭蜂鸣器
//	
//	Key_Init();
//	IO_Init();
//	
//	GPS_EN = 0;//关闭GPS
//
//	delay_ms(1000);
//	delay_ms(1000);
//	delay_ms(1000);
//	delay_ms(1000);
//	delay_ms(1000);
//	
//	delay_ms(1000);
//	delay_ms(1000);
//	delay_ms(1000);
//	delay_ms(1000);
//	delay_ms(1000);
//	
//	SendAlarmMessage();
//	SendFallDownMessage();
//	
//	GPS_EN = 0;
//	
//	SendGSMCommand("at+csclk=1\r\n");//休眠
//	
//	for(i=0; i<5; i++)
//			{
//				LED = 0;
//				delay_ms(100);
//				LED = 1;
//				delay_ms(100);
//			}
//	
//	BUZZER = 0;
//	delay_ms(100);
//	BUZZER = 1;
//	delay_ms(100);
//	BUZZER = 0;
//	delay_ms(100);
//	BUZZER = 1;

	initialize();
	
	BUZZER = 0;
	delay_ms(200);
	BUZZER = 1;
	delay_ms(50);
	BUZZER = 0;
	delay_ms(200);
	BUZZER = 1;
	delay_ms(50);
	BUZZER = 0;
	delay_ms(200);
	BUZZER = 1;
	
	while(1)
	{
		ReadAll();//读取mpu6050所有加速度和角速度数据
		GetData();//合成mpu6050加速度和角速度数据
		SendArray(mpudata, 16);
		
		if(FallDown())
		{
			BUZZER = 0;
			delay_ms(100);
			BUZZER = 1;
			delay_ms(100);
			BUZZER = 0;
			delay_ms(100);
			BUZZER = 1;
		}
	}

/*
	while(1)
	{
		i = Key_Scan();//按键函数放在外面
		
		if(i == 1)
		{
			BUZZER = 0;
			delay_ms(100);
			BUZZER = 1;
			i = 0;
		}
		
		if(i == 2)
		{
			BUZZER = 0;
			delay_ms(300);
			BUZZER = 1;
			i = 0;
		}
		
		ReadAll();//读取mpu6050所有加速度和角速度数据
		GetData();//合成mpu6050加速度和角速度数据
		//SendArray(mpudata, 16);
		//SendArray(USART2_RX_BUF, USART2_RX_STA & 0x7FFF);
		//SendArray(USART_RX_BUF, USART_RX_STA & 0x7FFF);
		
		if(FallDown())
		{
			BUZZER = 0;
			delay_ms(100);
			BUZZER = 1;
			delay_ms(100);
			BUZZER = 0;
			delay_ms(100);
			BUZZER = 1;
		}
		delay_ms(2);
		

		if(GSMReady())
		{
			s = 1;
		}
		
		if(s==1)
		{
			SendGSMCommand("at+csclk=1\r\n");
			delay_ms(100);
			SendFallDownMessage();
			s=2;
			
			BUZZER = 0;
			delay_ms(200);
			BUZZER = 1;
		}
		if(s==2)
		{
			GSM_EN = 0;
			SendGSMCommand("at+csclk=0\r\n");
			s=3;
			
			BUZZER = 0;
			delay_ms(200);
			BUZZER = 1;
			delay_ms(100);
			BUZZER = 0;
			delay_ms(200);
			BUZZER = 1;
		}
		if(s==3)
		{
			SendAlarmMessage();
			
			BUZZER = 0;
			delay_ms(200);
			BUZZER = 1;
			delay_ms(100);
			BUZZER = 0;
			delay_ms(200);
			BUZZER = 1;
			delay_ms(100);
			BUZZER = 0;
			delay_ms(200);
			BUZZER = 1;
		}
		
//		if(USART_RX_STA & 0x8000)
//		{
//			USART_RX_STA = 0;
//			for(i=0; i<10; i++)
//			{
//				USART3->DR = USART_RX_BUF[i];
//				while ((USART3->SR & 0x40)==0)
//				{
//				}
//			}
//		}
		
		
//			for(i=0; i<5; i++)
//			{
//				USART3->DR = 'G';
//				while ((USART3->SR & 0x40)==0)
//				{
//				}
//			}
			
//				LED = 0;
//				delay_ms(200);
//				LED = 1;
//				delay_ms(200);
//				LED = 0;
//				delay_ms(200);
//				LED = 1;
//				delay_ms(200);
	}
*/

/*	
	while(1)
	{
//		LED = 1;
//		delay_ms(400);
//		LED = 0;
//		delay_ms(400);
		
		ReadAll();//读取mpu6050所有加速度和角速度数据
		GetData();//合成mpu6050加速度和角速度数据
		SendArray(mpudata, 16);
		//SendArray(USART2_RX_BUF, USART2_RX_STA & 0x7FFF);
		//SendArray(USART_RX_BUF, USART_RX_STA & 0x7FFF);
		
		if(FallDown())
		{
			for(i=0; i<5; i++)
			{
				LED = 0;
				delay_ms(100);
				LED = 1;
				delay_ms(100);
			}
		}
		delay_ms(2);
	}
*/
/*
	while(1)
	{

//		LED = 1;
//		delay_ms(400);
//		LED = 0;
//		delay_ms(400);
	
		
		ReadAll();//读取mpu6050所有加速度和角速度数据
		GetData();//合成mpu6050加速度和角速度数据
		//SendMpu(mpudata, 12);
		
		if(FallDown())
		{
			LED = 0;//点亮led
			
		}
	
		
		if(USART2_RX_STA & 0x8000)
		{
			//printf("\n");
			//SendArray(USART2_RX_BUF, USART2_RX_STA & 0x7FFF);
			
			//printf("## 001 ##\n");
			if(getgps(USART2_RX_BUF))
			{
				printf("get gps\n");
				printf("N:%f E:%f \n",ChangeCoord(latitude), ChangeCoord(longtitude));
				SendLocationMessage();
				
			}
			else
			{
				printf("no gps\n");
			}
			
			USART2_RX_STA &= 0x0000;
		}
		else
		{
			printf("not get com2\n");
		}
		
	}
	*/
}

void SendArray(unsigned char *ArrayName, unsigned short int length)
{
	unsigned short int i;
	unsigned char sum = 0;

	//发送数据
	for (i=0; i<length; i++)
	{
		sum += ArrayName[i];
		USART3->DR = ArrayName[i];
		while ((USART3->SR & 0x40)==0)
		{
		}
	}
	
	USART3->DR = sum;
	while ((USART3->SR & 0x40)==0)
	{
	}
}

void initialize()
{
	short int i;
	
	Stm32_Clock_Init(2);//倍频设置为2，即主频16MHz
	delay_init(16);//延时初始化
	
	//只有串口1是全速，其它串口的速度都是串口1的一半
	uart_init(16, 9600);//用于GSM模块
	USART2_Init(8, 9600);//用于GPS模块
	USART3_Init(8, 9600);//用于测试
	
	//发送数据前读取USART1->SR，避免硬件复位后串口发送数据第一字节丢失的情况发生
	i = USART1->SR;
	i = USART2->SR;
	i = USART3->SR;
	
	//转换电话号码的格式
	ChangeNumber(GSM_AN, AddresseeNumber);
	ChangeNumber(GSM_SSCN, ServiceCenterNumber);
	
	LED_Init();//led接口初始化
	LED = 1;//熄灭led
	
	Buzzer_Init();//蜂鸣器接口初始化
	BUZZER = 1;//关闭蜂鸣器

	Key_Init();//按键初始化
	IO_Init();//GPS、GSM使能端初始化
	
	GPS_EN = 0;//关闭GPS
	
	delay_ms(500);//mpu6050初始化前加一定延时
	
	IICinit();//iic初始化
	initmpu6050();//mpu6050初始化，但是必须等gsm完全开启后再工作
	
//	delay_ms(1000);
//	delay_ms(1000);
//	delay_ms(1000);
//	delay_ms(1000);
//	delay_ms(1000);
//	
//	delay_ms(1000);
//	delay_ms(1000);
//	delay_ms(1000);
//	delay_ms(1000);
//	delay_ms(1000);

	BUZZER = 0;
	delay_ms(80);
	BUZZER = 1;
	delay_ms(40);
	BUZZER = 0;
	delay_ms(80);
	BUZZER = 1;

//gsm模块可能会初始化失败，应当考虑完善一下
	while(!GSMReady());//在这里等待，直到gsm模块初始化完成
}

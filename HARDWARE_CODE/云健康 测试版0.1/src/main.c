/***************************************************************
云健康系统1.0测试版

这个代码是从跌倒检测复制过来的，注意iic的接口

需不需要按键？

接口说明：
PA2 USART2 TX
PA3 USART2 RX

PA4
PA5
PA7

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

PB13 
PB15 

PD2 指示灯

测试硬件：
led
血糖
血氧
心电
温度
粉尘


目前存在的问题：


***************************************************************/

#include "sys.h"
#include "delay.h"
#include "key.h"
#include "led.h"
#include "usart.h"
#include "usart2.h"
#include "usart3.h"

#include "adc.h"
#include "STMIIC.h"
#include "SendPack.h"
//#include ""
//#include ""

#include "gsm.h"
#include "gps.h"
#include "mpu6050.h"
#include "falldown.h"
#include "buzzer.h"
#include "io.h"
//#include "STMIIC.h"
#include <math.h>

//云健康必须的文件
/*
adc.h
iic
sendpack
*/

void SendArray(unsigned char *ArrayName, unsigned short int length);
unsigned char initialize(void);



int main(void)
{
//	short int a,b,c;
	unsigned char i, f = 0;

	initialize();
	
	delay_ms(1000);
	
//	GSMSleep();
	
//	if(GSMSleep())
//	{
//		USART3->DR = 'S';
//		while ((USART3->SR & 0x40)==0)
//		{
//		}
//	}

//	//gps使能后，等待300毫秒让它把$GPTXT信息发完，之后收到的数据全都是$GPRMC定位信息
//	GPS_EN = 1;
//	delay_ms(300);
//	USART2_RX_STA = 0;

/*	
	while(1)
	{
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
			
			USART2_RX_STA = 0;
		}
	}
*/


	
	while(1)
	{
		//i = Key_Scan();//按键函数放在外面
		
		ReadAll();//读取mpu6050所有加速度和角速度数据
		GetData();//合成mpu6050加速度和角速度数据
		delay_ms(2);
		//SendArray(mpudata, 16);
		f = FallDown();
		
		if(KeyStatus == 1)
		{
			KeyStatus = 0;
			
			BUZZER = 0;
			delay_ms(100);
			BUZZER = 1;
			
			i = 0;
			
//			if(GSMWakeup())
//			{
//				USART3->DR = 'W';
//				while ((USART3->SR & 0x40)==0)
//				{
//				}
//			}
			
			//gps使能后，等待300毫秒让它把$GPTXT信息发完，之后收到的数据全都是$GPRMC定位信息
			GPS_EN = 1;
			delay_ms(300);
			USART2_RX_STA = 0;
			
			//SendFallDownMessage();
			
			while(1)
			{
				if(USART2_RX_STA & 0x8000)
				{
					//SendArray(USART2_RX_BUF, USART2_RX_STA & 0x7FFF);
					
					if(getgps(USART2_RX_BUF))
					{
						//printf("get gps\n");
						//printf("N:%f E:%f \n",ChangeCoord(latitude), ChangeCoord(longtitude));
						GPS_EN = 0;
						
						SendLocationMessage();
						break;
					}
					
					USART2_RX_STA = 0;
				}
				
//				if(USART2_RX_STA & 0x8000)
//				{
//					for (i=0; i<40; i++)
//					{
//						USART3->DR = USART2_RX_BUF[i];
//						while ((USART3->SR & 0x40)==0)
//						{
//						}
//					}
//				}
//				
//				if(getgps(USART2_RX_BUF))
//				{
//					//printf("get gps\n");
//					//printf("N:%f E:%f \n",ChangeCoord(latitude), ChangeCoord(longtitude));
//					GPS_EN = 0;
//					SendLocationMessage();
//					break;
//				}
			}
			
			BUZZER = 0;
			delay_ms(400);
			BUZZER = 1;
						
			//SendLocationMessage();
		}
		
		if(KeyStatus == 2)
		{
			KeyStatus = 0;
			
			BUZZER = 0;
			delay_ms(300);
			BUZZER = 1;
			i = 0;
			
		//	SendFallDownMessage();
		}
		
		if(f)
		{
			for(i=0; i<20; i++)
			{
				if(KeyStatus == 1)
				{
					KeyStatus = 0;
					break;
				}
				
				BUZZER = 0;
				delay_ms(400);
				BUZZER = 1;
				delay_ms(200);
			}
			//GSMWakeup();
			//SendFallDownMessage();
			f = 0;
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

//启动成功返回0
//gsm模块启动失败返回1
unsigned char initialize()
{
	unsigned short int i;
	
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
	GSM_EN = 1;//使能GSM
	GSM_RST = 1;//不复位GSM
	
	delay_ms(500);//mpu6050初始化前加一定延时
	
	IICinit();//iic初始化
	initmpu6050();//mpu6050初始化，但是必须等gsm完全开启后再工作

	BuzzerWork(80,40,2);//短鸣2声，系统启动成功


	//在这里等待，直到gsm模块初始化完成
	i = 0;
	while(!GSMReady())
	{
		delay_ms(1);
		i++;
		
		//半分钟内没动静表示gsm模块启动超时或失败
		//启动失败，则gsm模块复位重启，直到启动成功
		if(i > 30000)
		{
			BuzzerWork(100,50,5);//短鸣5声，gsm模块启动超时或失败
			GSM_RST = 0;
			delay_ms(10);
			GSM_RST = 1;
			i = 0;
		}
	}
	
	BuzzerWork(200,80,3);//长鸣3声，所有模块启动成功
	
	//启动成功后令gsm模块休眠
	//SendGSMCommand("at+csclk=1\r\n");
	//if(休眠成功)，待补充
	
	return 0;//gsm模块启动成功
}

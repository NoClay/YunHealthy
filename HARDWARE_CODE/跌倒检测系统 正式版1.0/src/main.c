/***************************************************************
跌倒检测系统1.2测试版

功能说明：
未跌倒时单击按键蜂鸣器提示一下，没有任何实际用途
未跌倒时长按按键进行紧急报警，此报警不可取消
跌倒后报警时单击按键可取消报警
跌倒后半分钟内不单击按键自动发送报警短信
定位短信发送完毕后会报警400ms


如果想要做好，还有很多细节需要考虑
比如，mpu6050意外停机
gsm意外关机
电池低压报警
stm32意外重启
看门狗
各种操作失败
……

说明：
gsm模块手动设定为关闭自动设置波特率，手动设置波特率为9600

按键一定要放在外部中断上！！！！！
按键一定要放在外部中断上！！！！！
按键一定要放在外部中断上！！！！！

提示音说明：
短鸣2声，系统启动成功
短鸣5声，gsm模块启动超时或失败
长鸣3声，所有模块启动成功

接口说明：
PA2 USART2 TX
PA3 USART2 RX

PA4 GPS使能 低电平休眠
PA5 GSM使能 低电平唤醒
PA7 GSM复位 低电平复位

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
led 完成
蜂鸣器 完成
按键 完成
usart1 完成
usart2 相对STM32接收正常，发送未测试也不必测试
usart3 相对STM32发送正常，接收未测试
mpu6050 完成
gsm 可工作，接入电流表后经常启动失败
gps 完成
gsm使能 完成
gps使能 完成
gsm休眠及唤醒 休眠失败、唤醒失败
gps休眠及唤醒 完成
按键中断 完成


目前存在的问题：
gsm模块在启动、发送短信、接打电话时会给mpu6050带来巨大干扰！！！！！
gsm模块在启动、发送短信、接打电话时会给mpu6050带来巨大干扰！！！！！
gsm模块在启动、发送短信、接打电话时会给mpu6050带来巨大干扰！！！！！

gsm模块启动成功后，仍会给6050带来轻微干扰，具体表现为6050波形有毛刺

偶尔会发生gsm启动成功，但提示失败

gps信号不好时，可能会出现gps指示灯闪烁提示定位成功，但不发送定位短信的情况

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
void NoGsmInitialize(void);
void ThisSendFalldownMessage(void);
void ThisSendAlarmMessage(void);


int main(void)
{
	unsigned char fall = 0;//跌倒标识

	initialize();
//	NoGsmInitialize();
	
	delay_ms(1000);
	
	while(1)
	{
		ReadAll();//读取mpu6050所有加速度和角速度数据
		GetData();//合成mpu6050加速度和角速度数据
		delay_ms(2);
		//SendArray(mpudata, 16);
		fall = FallDown();
		
		if(KeyStatus == 1)
		{
			KeyStatus = 0;
			
			BUZZER = 0;
			delay_ms(100);
			BUZZER = 1;
		}
		
		if(KeyStatus == 2)
		{
			KeyStatus = 0;//按键状态置0

			//蜂鸣器报警
			BUZZER = 0;
			delay_ms(800);
			BUZZER = 1;
			
			ThisSendAlarmMessage();
		}
		
		if(fall)
		{
			fall = 0;
			
			ThisSendFalldownMessage();
		}
	}
}

//测试时用于向上位机发送数据
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

//初始化
void initialize(void)
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
	
	//GSM休眠暂时无法实现
	//启动成功后令gsm模块休眠
	//GSMSleep();
}

//初始化，不等待gsm，快速启动，方便调试其他功能
void NoGsmInitialize(void)
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


//	//在这里等待，直到gsm模块初始化完成
//	i = 0;
//	while(!GSMReady())
//	{
//		delay_ms(1);
//		i++;
//		
//		//半分钟内没动静表示gsm模块启动超时或失败
//		//启动失败，则gsm模块复位重启，直到启动成功
//		if(i > 30000)
//		{
//			BuzzerWork(100,50,5);//短鸣5声，gsm模块启动超时或失败
//			GSM_RST = 0;
//			delay_ms(10);
//			GSM_RST = 1;
//			i = 0;
//		}
//	}
//	
//	BuzzerWork(200,80,3);//长鸣3声，所有模块启动成功
//	
//	//GSM休眠暂时无法实现
//	//启动成功后令gsm模块休眠
//	//GSMSleep();
}

//跌倒报警、发送报警短信、定位及发送定位短信的全部流程
void ThisSendFalldownMessage(void)
{
	unsigned char i;
	
	//通过修改循环次数和蜂鸣器工作时间来控制报警时间
	for(i=0; i<30; i++)
	{
		if(KeyStatus == 1)
		{
			KeyStatus = 0;
			return;
		}
		
		BUZZER = 0;
		delay_ms(500);
		BUZZER = 1;
		delay_ms(300);
	}
	
	//GSM唤醒及休眠暂时无法实现
	//GSMWakeup();
	
	SendFallDownMessage();
	
	//gps使能后，等待300毫秒让它把$GPTXT信息发完，之后收到的数据全都是$GPRMC定位信息
	GPS_EN = 1;
	delay_ms(300);
	USART2_RX_STA = 0;
	
	while(1)
	{
		if(USART2_RX_STA & 0x8000)
		{
			//SendArray(USART2_RX_BUF, USART2_RX_STA & 0x7FFF);
			if(getgps((char *)USART2_RX_BUF))
			{
				GPS_EN = 0;
				SendLocationMessage();
				break;
			}
			USART2_RX_STA = 0;
		}
	}
	
	BUZZER = 0;
	delay_ms(400);
	BUZZER = 1;
}

//紧急报警、发送报警短信、定位及发送定位短信的全部流程
void ThisSendAlarmMessage(void)
{	
	SendAlarmMessage();
	
	//gps使能后，等待300毫秒让它把$GPTXT信息发完，之后收到的数据全都是$GPRMC定位信息
	GPS_EN = 1;
	delay_ms(300);
	USART2_RX_STA = 0;
				
	while(1)
	{
		if(USART2_RX_STA & 0x8000)
		{
			//SendArray(USART2_RX_BUF, USART2_RX_STA & 0x7FFF);
			
			if(getgps((char *)USART2_RX_BUF))
			{
				GPS_EN = 0;
				SendLocationMessage();
				break;
			}
			USART2_RX_STA = 0;
		}
	}
	
	BUZZER = 0;
	delay_ms(400);
	BUZZER = 1;
}

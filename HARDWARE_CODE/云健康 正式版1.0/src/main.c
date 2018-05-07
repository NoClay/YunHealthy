/***************************************************************
云健康系统1.0正式版

血糖仪和温度模块平时不要同时连接
读取血糖数据时一定一定一定一定一定一定一定不能连接温度模块
I2C总线有冲突！！！！！！！

血糖仪连接好以后，按key0读取数据


串口5的pd2上还有个led，可能会对通信造成影响！！！！！！！！！！！！！！！！！！！！！！！！！！！！！

接口使用：
PA1 AD 心电
PA2 USART2 TX 空
PA3 USART2 RX 粉尘
PA4 SDA
PA5 SCL
PA9 USART1 TX 蓝牙
PA10 USART1 RX 血氧

IIC接口：
SDA PA4
SCL PA5

串口使用规划：
串口1：血氧
串口2：空气
串口3：该引脚被液晶屏占用，重映射还是被占用
串口4：该引脚被液晶屏占用，重映射还是被占用
串口5：能发，不能收，简直……


串口位置：
USART1 RX PA10
USART1 TX PA9

USART2 RX PA3
USART2 TX PA2

USART3 RX PB11
USART3 TX PB10

UART4 RX P11
UART4 TX P10

UART5 RX PD2
UART5 TX PC12


液晶屏占用的引脚：
全部PB
PC6至PC10

触摸屏占用的引脚：
PC0至PC3，PC13

板载按键占用的引脚：
WK_UP PA0
KEY0 PC5
KEY1 PA15

板载LED占用的引脚：
DS0 PA9
DS1 PD2

其他接口：
PA13 SWDIO
PA14 SWCLK

PB6 板载24C02的SCL
PB7 板载24C02的SDA

字符取模方式：
阴码，顺向，逐行

说明：
串口2的函数被我修改了

目前存在的问题：
GUI美化，很头疼的一个问题
血流灌注指数的解析有问题，反正一般人也不知道这个参数是干嘛的，先不要了。
基本功能全部完成，但是界面不够友好，部分传感器没有连接时应当给用户一定提示。
如果血糖数据同步失败，本地应当处理一下，上位机也要做进一步处理，判断数据格式是否正确。

***************************************************************/
//是否通过串口发送所有数据，方便调试时只看自己想要的数据
//0不发送，1发送
//正式发布时一定要改为1
#define SendAllData 1

#include "sys.h"
#include "usart.h"		
#include "delay.h"	
#include "led.h"   
#include "lcd.h"
#include "math.h"
#include "adc.h"
#include "SendPack.h"
#include "usart2dust.h"
#include "STMIIC.h"
#include "MLX90614.h"
#include "chinese.h"//中文字库
#include "key.h"
#include "read24c16.h"

//------------------------------函数声明------------------------------//

void DrawECG(void);//绘制心电图
void GetSpO2(unsigned char *p);//读取血糖
void DisplayInit(void);//绘制不变的文字，绘制心电图的表格

//显示中文字库中的汉字
//一次显示一个汉字
//字符取模方式：阴码，顺向，逐行
//参数：x轴位置，y轴位置，字库中字符的行号(序号)
void LCD_ShowChinese(unsigned short int x, unsigned short int y, unsigned short int word);
void LCD_ShowChinese24(unsigned short int x, unsigned short int y, unsigned short int word);

//------------------------------变量声明------------------------------//

unsigned short int count = 0;//一个计数器

//以下部分是需要测量的参数
unsigned short int ECG_axis = 0, ECG_value_old = 0, ECG_value_new = 0;//心电图的横坐标，旧、新心电值
unsigned char TempInt = 0, TempFlo = 0;//温度的整数、小数部分
unsigned char SpO2 = 0, Pulse = 0;//血氧，脉搏
unsigned short int PI = 0;//Perfusion Index血流灌注指数的缩写PI
float temperature = 0.0, dust =0.0;//温度，粉尘浓度
unsigned int intdust = 0;//int型的粉尘浓度

//以下都是蓝牙发送数据的数组
unsigned char ECG_data[2] = {0};//心电
unsigned char Temp_data[2] = {0};//温度
unsigned char dust_data[4] = {0};//粉尘
unsigned char SpO2_data[4] = {0};//血氧、脉搏、灌注指数
unsigned char BG_data[1320] = {0};//血糖，Blood Glucose，血液中的葡萄糖
unsigned char BG_data_source[2048] = {0};//血糖，Blood Glucose，血液中的葡萄糖

//------------------------------Let's GO!------------------------------//

int main(void)
{
  	Stm32_Clock_Init(9);//系统时钟设置
	uart_init(72, 115200);//串口1初始化为115200，这是血氧仪的通信速率，这个函数的位置不要改，否则会出问题
	delay_init(72);
	LED_Init();
 	LCD_Init();
	USART2_Init(36, 2400);//串口2初始化为2400，这是粉尘浓度检测仪的通信速率
	Adc_Init();
	IICinit();
	Key_Init();
	DisplayInit();
	
  	while(1)
	{
		delay_ms(6);
		count++;
		
		//心电数据采集
		ECG_value_new = Get_Adc(ADC_CH1);//读心电
		#if SendAllData
			//先发送心电数据后绘图
			ECG_data[0] = ECG_value_new >> 8;
			ECG_data[1] = ECG_value_new;
			Com1SendArray(ECG_data , 0xE2, 2, 1);
		#endif
		DrawECG();//画心电图
		
		//粉尘数据采集
		if(count == 50)
		{
			//手册里的粉尘浓度计算公式
			dust = (float)((Com2RecArr[1] << 8) + Com2RecArr[2]) * 2.686;

			Com2RecComplete = 0;//串口接收后需手动清零
			
			#if SendAllData
				//发送粉尘数据，粉尘数据扩大100倍，相当于保留两位小数
				dust_data[0] = ((int)((dust) * 100)) >> 24;
				dust_data[1] = ((int)((dust) * 100)) >> 16;
				dust_data[2] = ((int)((dust) * 100)) >> 8;
				dust_data[3] = ((int)((dust) * 100));
				Com1SendArray(dust_data, 0xE5, 4, 1);
			#endif
			
			//显示粉尘数据
			intdust = (int)dust * 100;
			intdust /= 100;
			LCD_ShowNum(240, 40, intdust, 3, 16);	
		}
		
		//温度数据采集
		if(count == 100)
		{
			count = 0;
			
			temperature = ReadTemperatureMLX90614();
			#if SendAllData
				//发送体温数据，体温数据扩大100倍，相当于保留两位小数
				Temp_data[0] = ((int)((temperature) * 100)) >> 8;
				Temp_data[1] = ((int)((temperature) * 100));
				Com1SendArray(Temp_data, 0xE4, 2, 1);
			#endif
			
			//显示温度
			TempInt = (int)temperature;
			TempFlo = (int) (temperature * 10) % 10;
			LCD_ShowNum(60, 40, TempInt, 3, 16);
			LCD_ShowNum(90, 40, TempFlo, 1, 16);
		}
		
		//血氧数据采集
		if(USART_RX_STA & 0x8000)//如果串口1收到数据
		{
			GetSpO2(USART_RX_BUF);//解析血氧数据
			
			#if SendAllData
				//发送血氧、脉搏、血流灌注指数，（血流灌注指数没解析出来，填0）
				SpO2_data[0] = SpO2;
				SpO2_data[1] = Pulse;
				SpO2_data[2] = 0;
				SpO2_data[3] = 0;
				Com1SendArray(SpO2_data, 0xE1, 4, 1);
			#endif
			
			//显示血氧、脉搏
			LCD_ShowNum(60, 60, SpO2, 2, 16);
			LCD_ShowNum(200, 60, Pulse, 3, 16);
		}
		
		//手动读取血糖数据
		if(Key_Scan(0) == 1)
		{
			//读取血糖
			ReadBG(BG_data_source);
			
			LCD_ShowChinese(160, 80, 23);//显示“读”字
			LCD_ShowChinese(176, 80, 24);//显示“取”字
			LCD_ShowChinese(224, 80, 29);//显示“！”字
			
			//这是一个比较有标志性的字节，用这个来判断是否正常读出
			if(BG_data_source[2004] == 0x00 || BG_data_source[2004] == 0xFF)
			{
				//显示“读取失败！”
				LCD_ShowChinese(192, 80, 27);//显示“失”字
				LCD_ShowChinese(208, 80, 28);//显示“败”字
				
			}
			else
			{
				//显示“读取成功”
				LCD_ShowChinese(192, 80, 25);//显示“成”字
				LCD_ShowChinese(208, 80, 26);//显示“功”字
				
				DealBG(BG_data_source, BG_data);
				Com1SendArray(BG_data, 0xE3, 1320, 1);
			}
			//擦除
			delay_ms(1000);
			LCD_Fill(160, 80, 240, 96, WHITE);
		}
	}
}


//--------------------------------我是void main(void)分割线--------------------------------//


//血氧仪发送的数据格式
//U2:血氧,血糖,血流灌注指数\r\n
void GetSpO2(unsigned char *p)
{
	unsigned char i=0, j=1;//j用于记录已经找到的逗号的个数
	unsigned char e;//乘方运算的指数
	unsigned char comma[5] = {0};//用于保存逗号的位置，为了方便处理把0空着

	//第一个符号是冒号，就把它当成逗号，反正它在字符串中的位置固定是2，直接定死
	comma[1] = 2;
	j = 2;//从第二个逗号开始找
	
	//找出所有逗号的位置
	for(i=0; i<((USART_RX_STA & 0x3FFF)); i++)
	{
		if(p[i] == 44)//44是逗号的ASCII码
		{
			comma[j] = i;
			j++;
		}
		
//		if(p[i] == 0x0D)//把0x0D也当成逗号，用于处理血流灌注指数
//		{
//			comma[j] = i;
//			j++;
//		}
	}
	
	//第1、2逗号之间保存的是血氧
	SpO2 = 0;//先清零
	e = comma[2] - comma[1] - 2;//数值的总位数，为什么是减2，一位是逗号，10的0次方才是1，所以还要再减一
	for(i=comma[1]+1; i<comma[2]; i++)//comma[1]+1才是数字开始的地方
	{
		//从高位开始把每一位都累加起来
		//ascii码中48~57是数字
		SpO2 = (int)(SpO2 + (p[i] - 48) * pow(10, e));
		e--;	
	}
	
	//第2、3逗号之间保存的是脉搏
	Pulse = 0;//先清零
	e = comma[3] - comma[2] - 2;//数值的总位数，为什么是减2，一位是逗号，10的0次方才是1，所以还要再减一
	for(i=comma[2]+1; i<comma[3]; i++)//comma[1]+1才是数字开始的地方
	{
		//从高位开始把每一位都累加起来
		//ascii码中48~57是数字
		Pulse = (int)(Pulse + (p[i] - 48) * pow(10, e));
		e--;	
	}
	
	
//第3、4逗号之间保存的是血流灌注指数
//这个解析有问题，先不要了
//	PI = 0;//先清零
//	e = comma[4] - comma[3] - 2;//数值的总位数，为什么是减2，一位是逗号，10的0次方才是1，所以还要再减一
//	for(i=comma[3]+1; i<comma[4]; i++)//comma[1]+1才是数字开始的地方
//	{
//		//从高位开始把每一位都累加起来
//		//ascii码中48~57是数字
//		PI = (int)(PI + (p[i] - 48) * pow(10, e));
//		e--;
//	}
	
	//接收完成以后一定要手动清零！！
	USART_RX_STA = 0;
}

void LCD_ShowChinese(unsigned short int x, unsigned short int y, unsigned short int word)//一次显示一个汉字
{
	unsigned char tmp;
	unsigned short i, j, k;//ij用于循环，k用于控制汉字图形数组
	
	k = 0;
	for(i=0; i<16; i++)
	{
		tmp = CHS[word][k];
		for(j=0; j<8; j++)
		{
			if(tmp & 0x80)
			{
				LCD_Fast_DrawPoint(x+j, y+i, RED);
			}
			tmp <<= 1;
		}
		k++;
		
		tmp = CHS[word][k];
		for(j=8; j<16; j++)
		{
			if(tmp & 0x80)
			{
				LCD_Fast_DrawPoint(x+j, y+i, RED);
			}
			tmp <<= 1;
		}
		k++;
	}
}

void LCD_ShowChinese24(unsigned short int x, unsigned short int y, unsigned short int word)//一次显示一个汉字
{
	unsigned char tmp;
	unsigned short i, j, k;//ij用于循环，k用于控制汉字图形数组
	
	k = 0;
	for(i=0; i<24; i++)
	{
		tmp = CHS24[word][k];
		for(j=0; j<8; j++)
		{
			if(tmp & 0x80)
			{
				LCD_Fast_DrawPoint(x+j, y+i, BLUE);
			}
			tmp <<= 1;
		}
		k++;
		
		tmp = CHS24[word][k];
		for(j=8; j<16; j++)
		{
			if(tmp & 0x80)
			{
				LCD_Fast_DrawPoint(x+j, y+i, BLUE);
			}
			tmp <<= 1;
		}
		k++;
		
		tmp = CHS24[word][k];
		for(j=16; j<24; j++)
		{
			if(tmp & 0x80)
			{
				LCD_Fast_DrawPoint(x+j, y+i, BLUE);
			}
			tmp <<= 1;
		}
		k++;
	}
}

void DrawECG(void)
{
	unsigned short int i;
	
	ECG_value_new >>= 5;//将心电值缩小32倍，心电的范围就在129以内，方便画图
	LCD_Fill(ECG_axis, 110, ECG_axis + 3, 240, WHITE);//把要画线的前面的区域填充为白色，相当于擦除以前的图形
	
	for(i=110; i<241; i += 10)
	{
		if(ECG_axis % 10)
		{
			LCD_Fast_DrawPoint(ECG_axis, i, GREEN);//画横线
		}
		else
		{
			POINT_COLOR=GREEN;
			LCD_DrawLine(ECG_axis, 110, ECG_axis, 240);//画纵线
			POINT_COLOR=RED;
		}
	}

	LCD_DrawLine(ECG_axis, 110 + ECG_value_old, ECG_axis + 1, 110 + ECG_value_new);//绘制心电图
	ECG_value_old = ECG_value_new;//绘制完成以后这就相当于是旧数据
	
	ECG_axis++;
	if(ECG_axis > 320)
	{
		ECG_axis = 0;
	}
}

void DisplayInit(void)
{
	unsigned short int i;
	POINT_COLOR = RED;
	
	//显示“云健康远程监护仪”
	for(i=0; i<8; i++)
	{
		LCD_ShowChinese24(i * 24 + 64, 0, i);
	}
	
	LCD_ShowChinese(20, 40, 0);//显示“温”字
	LCD_ShowChinese(36, 40, 1);//显示“度”字
	LCD_ShowChinese(52, 40, 18);//显示“：”字
	LCD_ShowString(84, 40, 8, 16, 16, ".");//这是温度的小数点
	LCD_ShowChinese(98, 40, 17);//显示“℃”字
	
	LCD_ShowChinese(160, 40, 9);//显示“粉”字
	LCD_ShowChinese(176, 40, 10);//显示“尘”字
	LCD_ShowChinese(192, 40, 11);//显示“浓”字
	LCD_ShowChinese(208, 40, 12);//显示“度”字
	LCD_ShowChinese(224, 40, 18);//显示“：”字
	LCD_ShowChinese(266, 40, 21);//显示“ug”微克
	LCD_ShowChinese(282, 40, 22);//显示“/m^3”每立方米
	
	LCD_ShowChinese(20, 60, 2);//显示“血”字
	LCD_ShowChinese(36, 60, 3);//显示“氧”字
	LCD_ShowChinese(52, 60, 18);//显示“：”字
	LCD_ShowString(78, 60, 8, 16, 16, "%");//显示“%”字
	
	LCD_ShowChinese(160, 60, 4);//显示“脉”字
	LCD_ShowChinese(176, 60, 5);//显示“搏”字
	LCD_ShowChinese(192, 60, 18);//显示“：”字
	LCD_ShowChinese(226, 60, 19);//显示“次”字
	LCD_ShowString(242, 60, 8, 16, 16, "/");//显示“/”字
	LCD_ShowChinese(250, 60, 20);//显示“分”字

	LCD_ShowChinese(10, 90, 6);//显示“心”字
	LCD_ShowChinese(26, 90, 7);//显示“电”字
	LCD_ShowChinese(42, 90, 8);//显示“图”字
	LCD_ShowChinese(58, 90, 18);//显示“：”字
	
	//绘制心电表格
	while(ECG_axis < 321)
	{
		for(i=110; i<241; i += 10)
		{
			if(ECG_axis % 10)
			{
				LCD_Fast_DrawPoint(ECG_axis, i, GREEN);//画横线
			}
			else
			{
				POINT_COLOR=GREEN;
				LCD_DrawLine(ECG_axis, 110, ECG_axis, 240);//画纵线
				POINT_COLOR=RED;
			}
		}
		ECG_axis++;
	}
}

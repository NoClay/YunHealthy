#include "SendPack.h"
#include "usart.h"
#include "usart2.h"
#include "usart3.h"

//帧头 数据类型 数据长度 数据 校验和

//所有函数的格式都一样，四个参数分别为数组名，数据标识，数据长度，是否有校验和
void Com1SendArray(unsigned char *ArrayName, unsigned char DataType, unsigned short int length, unsigned char IfCheck)
{
	unsigned short int i;
	unsigned char info[5];
	unsigned char CheckSum = 0;
	
	info[0] = DATAHEAD1;
	info[1] = DATAHEAD2;
	info[2] = DataType;
	info[3] = length >> 8;//高八位
	info[4] = length & 0x00FF;//低八位
	
	/*
	//发送2字节的帧头
	USART1->DR = DATAHEAD1;
	while ((USART1->SR & 0x40)==0)
	{
	}
	USART1->DR = DATAHEAD2;
	while ((USART1->SR & 0x40)==0)
	{
	}
	
	//发送数据类型
	USART1->DR = DataType;
	while ((USART1->SR & 0x40)==0)
	{
	}
	
	//发送数据长度
	USART1->DR = length >> 8;//发送高八位
	while ((USART1->SR & 0x40)==0)
	{
	}
	USART1->DR = length & 0x00FF;//发送低八位
	while ((USART1->SR & 0x40)==0)
	{
	}
	*/
	if(IfCheck)
	{
		//发送各种信息
		for (i=0; i<5; i++)
		{
			CheckSum += info[i];//校验和
			USART1->DR = info[i];
			while ((USART1->SR & 0x40)==0)
			{
			}
		}

		//发送数据
		for (i=0; i<length; i++)
		{
			CheckSum += ArrayName[i];//校验和
			USART1->DR = ArrayName[i];
			while ((USART1->SR & 0x40)==0)
			{
			}
		}
		
		//发送校验和
		USART1->DR = CheckSum;
		while ((USART1->SR & 0x40)==0)
		{
		}
	}
	else
	{
		//发送各种信息
		for (i=0; i<5; i++)
		{
			USART1->DR = info[i];
			while ((USART1->SR & 0x40)==0)
			{
			}
		}

		//发送数据
		for (i=0; i<length; i++)
		{
			USART1->DR = ArrayName[i];
			while ((USART1->SR & 0x40)==0)
			{
			}
		}
	}
}

void Com2SendArray(unsigned char *ArrayName, unsigned short int length, unsigned char DataType, unsigned char IfCheck)
{
	unsigned short int i;
	unsigned char info[5];
	unsigned char CheckSum = 0;
	
	info[0] = DATAHEAD1;
	info[1] = DATAHEAD2;
	info[2] = DataType;
	info[3] = length >> 8;//高八位
	info[4] = length & 0x00FF;//低八位

	if(IfCheck)
	{
		//发送各种信息
		for (i=0; i<5; i++)
		{
			CheckSum += info[i];//校验和
			USART2->DR = info[i];
			while ((USART2->SR & 0x40)==0)
			{
			}
		}

		//发送数据
		for (i=0; i<length; i++)
		{
			CheckSum += ArrayName[i];//校验和
			USART2->DR = ArrayName[i];
			while ((USART2->SR & 0x40)==0)
			{
			}
		}
		
		//发送校验和
		USART2->DR = CheckSum;
		while ((USART2->SR & 0x40)==0)
		{
		}
	}
	else
	{
		//发送各种信息
		for (i=0; i<5; i++)
		{
			USART2->DR = info[i];
			while ((USART2->SR & 0x40)==0)
			{
			}
		}

		//发送数据
		for (i=0; i<length; i++)
		{
			USART2->DR = ArrayName[i];
			while ((USART2->SR & 0x40)==0)
			{
			}
		}
	}
}

void Com3SendArray(unsigned char *ArrayName, unsigned short int length, unsigned char DataType, unsigned char IfCheck)
{
	unsigned short int i;
	unsigned char info[5];
	unsigned char CheckSum = 0;
	
	info[0] = DATAHEAD1;
	info[1] = DATAHEAD2;
	info[2] = DataType;
	info[3] = length >> 8;//高八位
	info[4] = length & 0x00FF;//低八位

	if(IfCheck)
	{
		//发送各种信息
		for (i=0; i<5; i++)
		{
			CheckSum += info[i];//校验和
			USART3->DR = info[i];
			while ((USART3->SR & 0x40)==0)
			{
			}
		}

		//发送数据
		for (i=0; i<length; i++)
		{
			CheckSum += ArrayName[i];//校验和
			USART3->DR = ArrayName[i];
			while ((USART3->SR & 0x40)==0)
			{
			}
		}
		
		//发送校验和
		USART3->DR = CheckSum;
		while ((USART3->SR & 0x40)==0)
		{
		}
	}
	else
	{
		//发送各种信息
		for (i=0; i<5; i++)
		{
			USART3->DR = info[i];
			while ((USART3->SR & 0x40)==0)
			{
			}
		}

		//发送数据
		for (i=0; i<length; i++)
		{
			USART3->DR = ArrayName[i];
			while ((USART3->SR & 0x40)==0)
			{
			}
		}
	}
}

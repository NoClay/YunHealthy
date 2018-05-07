#include "SendPack.h"
#include "usart.h"
#include "usart2.h"
#include "usart3.h"

//֡ͷ �������� ���ݳ��� ���� У���

//���к����ĸ�ʽ��һ�����ĸ������ֱ�Ϊ�����������ݱ�ʶ�����ݳ��ȣ��Ƿ���У���
void Com1SendArray(unsigned char *ArrayName, unsigned char DataType, unsigned short int length, unsigned char IfCheck)
{
	unsigned short int i;
	unsigned char info[5];
	unsigned char CheckSum = 0;
	
	info[0] = DATAHEAD1;
	info[1] = DATAHEAD2;
	info[2] = DataType;
	info[3] = length >> 8;//�߰�λ
	info[4] = length & 0x00FF;//�Ͱ�λ
	
	/*
	//����2�ֽڵ�֡ͷ
	USART1->DR = DATAHEAD1;
	while ((USART1->SR & 0x40)==0)
	{
	}
	USART1->DR = DATAHEAD2;
	while ((USART1->SR & 0x40)==0)
	{
	}
	
	//������������
	USART1->DR = DataType;
	while ((USART1->SR & 0x40)==0)
	{
	}
	
	//�������ݳ���
	USART1->DR = length >> 8;//���͸߰�λ
	while ((USART1->SR & 0x40)==0)
	{
	}
	USART1->DR = length & 0x00FF;//���͵Ͱ�λ
	while ((USART1->SR & 0x40)==0)
	{
	}
	*/
	if(IfCheck)
	{
		//���͸�����Ϣ
		for (i=0; i<5; i++)
		{
			CheckSum += info[i];//У���
			USART1->DR = info[i];
			while ((USART1->SR & 0x40)==0)
			{
			}
		}

		//��������
		for (i=0; i<length; i++)
		{
			CheckSum += ArrayName[i];//У���
			USART1->DR = ArrayName[i];
			while ((USART1->SR & 0x40)==0)
			{
			}
		}
		
		//����У���
		USART1->DR = CheckSum;
		while ((USART1->SR & 0x40)==0)
		{
		}
	}
	else
	{
		//���͸�����Ϣ
		for (i=0; i<5; i++)
		{
			USART1->DR = info[i];
			while ((USART1->SR & 0x40)==0)
			{
			}
		}

		//��������
		for (i=0; i<length; i++)
		{
			USART1->DR = ArrayName[i];
			while ((USART1->SR & 0x40)==0)
			{
			}
		}
	}
}

void Com2SendArray(unsigned char *ArrayName, unsigned char DataType, unsigned short int length, unsigned char IfCheck)
{
	unsigned short int i;
	unsigned char info[5];
	unsigned char CheckSum = 0;
	
	info[0] = DATAHEAD1;
	info[1] = DATAHEAD2;
	info[2] = DataType;
	info[3] = length >> 8;//�߰�λ
	info[4] = length & 0x00FF;//�Ͱ�λ

	if(IfCheck)
	{
		//���͸�����Ϣ
		for (i=0; i<5; i++)
		{
			CheckSum += info[i];//У���
			USART2->DR = info[i];
			while ((USART2->SR & 0x40)==0)
			{
			}
		}

		//��������
		for (i=0; i<length; i++)
		{
			CheckSum += ArrayName[i];//У���
			USART2->DR = ArrayName[i];
			while ((USART2->SR & 0x40)==0)
			{
			}
		}
		
		//����У���
		USART2->DR = CheckSum;
		while ((USART2->SR & 0x40)==0)
		{
		}
	}
	else
	{
		//���͸�����Ϣ
		for (i=0; i<5; i++)
		{
			USART2->DR = info[i];
			while ((USART2->SR & 0x40)==0)
			{
			}
		}

		//��������
		for (i=0; i<length; i++)
		{
			USART2->DR = ArrayName[i];
			while ((USART2->SR & 0x40)==0)
			{
			}
		}
	}
}

void Com3SendArray(unsigned char *ArrayName, unsigned char DataType, unsigned short int length, unsigned char IfCheck)
{
	unsigned short int i;
	unsigned char info[5];
	unsigned char CheckSum = 0;
	
	info[0] = DATAHEAD1;
	info[1] = DATAHEAD2;
	info[2] = DataType;
	info[3] = length >> 8;//�߰�λ
	info[4] = length & 0x00FF;//�Ͱ�λ

	if(IfCheck)
	{
		//���͸�����Ϣ
		for (i=0; i<5; i++)
		{
			CheckSum += info[i];//У���
			USART3->DR = info[i];
			while ((USART3->SR & 0x40)==0)
			{
			}
		}

		//��������
		for (i=0; i<length; i++)
		{
			CheckSum += ArrayName[i];//У���
			USART3->DR = ArrayName[i];
			while ((USART3->SR & 0x40)==0)
			{
			}
		}
		
		//����У���
		USART3->DR = CheckSum;
		while ((USART3->SR & 0x40)==0)
		{
		}
	}
	else
	{
		//���͸�����Ϣ
		for (i=0; i<5; i++)
		{
			USART3->DR = info[i];
			while ((USART3->SR & 0x40)==0)
			{
			}
		}

		//��������
		for (i=0; i<length; i++)
		{
			USART3->DR = ArrayName[i];
			while ((USART3->SR & 0x40)==0)
			{
			}
		}
	}
}

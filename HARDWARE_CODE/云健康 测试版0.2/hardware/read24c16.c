#include "read24c16.h"
#include "STMIIC.h"

//��Ҫ�����ڴ����⣡������
void Readxt(unsigned char *p)
{
	unsigned short int i;
	
	//�Ȱѻ���������0xFF���
	for (i=0; i<2048; i++)
	{
		p[i] = 0xFF;
	}
	
	IICstart();
	IICwrite(0xa8);//ֱ�Ӵ�ͷ�����������Ժ�����취����
	//SlaRes();
	if(SlaRes())
	{
		printf("err 001\r\n");
	}
	else
	{
		printf("ok 001\r\n");
	}
	IICwrite(0x00);//ָ����ַ
	//SlaRes();
	if(SlaRes())
	{
		printf("err 002\r\n");
	}
	else
	{
		printf("ok 002\r\n");
	}
	
	IICstart();
	IICwrite(0xA1);//���Ͷ��ź�
	//SlaRes();
	if(SlaRes())
	{
		printf("err 003\r\n");
	}
	else
	{
		printf("ok 003\r\n");
	}
	
	for (i=0; i<2048; i++)
	{
		p[i] = IICread();
		MasRes();
	}
	
	NoRes();
	IICstop();
	
}

void deal(unsigned char *p, unsigned char *out)
{
	//ʹ��ǰһ��Ҫ���out���飡��������
	
	int i,j,n,x;
	
	n = p[2004];//������ݱ������Ѫ�����ݸ���
	//n = n << 8;//8�ֽ�Ϊһ�����ݣ�
	
	//6�ֽ�Ϊһ�飬��ȥ����У��λ��������
	for (i=0; i<n; i++)
	{
		for(j=0; j<6; j++)
		{
			out[x] = p[i*8+j];
		}
	}
}

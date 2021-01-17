import React from 'react'

import classNames from 'classnames/bind';
import GuavaBuildingInfo from './GuavaBuildingInfo';
import GuavaDetailHeader from '../header/GuavaHeader';
import GuavaTradeOption from './GuavaTradeOption';
import GuavaChart from './GuavaChart';
import GuavaTable from '../table/GuavaTable';

import styles from './guavaDetailPage.module.scss';
import {useRecoilValue} from 'recoil';
import {
    regionState,
} from '../datatool/state';
import {WhiteSpace} from 'antd-mobile';

const cx = classNames.bind(styles);

const GuavaSearchPage = () => {
    const region = useRecoilValue(regionState);

    return (
        <>
            {
                region &&
                <>
                    <GuavaDetailHeader/>
                    <GuavaBuildingInfo/>
                    <WhiteSpace/>
                    <GuavaTradeOption/>
                    <GuavaChart/>
                    <GuavaTable/>
                </>
            }
        </>
    );
};

export default GuavaSearchPage;
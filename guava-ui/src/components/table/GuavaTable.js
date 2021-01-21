import React, {useEffect, useState} from 'react'
import {useHistory} from 'react-router-dom';
import {useRecoilState, useRecoilValue} from 'recoil';
import InfiniteScroll from 'react-infinite-scroll-component';

import {areaTypeState, filterAreaState, regionState, tradeDateState, tradeTypeState} from '../datatool/state';
import {getRegionTrade, getTrade} from '../datatool/api';

import classNames from 'classnames/bind';
import styles from './guavaTable.module.scss';
import {getEndArea, getStartArea} from '../constant';
import GuavaTradeRow from './row/GuavaTradeRow';
import GuavaRegionTradeRow from './row/GuavaRegionTradeRow';
import GuavaTradeCol from './col/GuavaTradeCol';
import GuavaRegionTradeCol from './col/GuavaRegionTradeCol';
import GuavaLoading from '../detail/GuavaLoading';
import ArrowLeftOutlined from '@ant-design/icons/es/icons/ArrowLeftOutlined';

const cx = classNames.bind(styles);

const GuavaTable = () => {
    const history = useHistory();
    const [page, setPage] = useState(0);
    const [isLoading, setIsLoading] = useState(false);
    const [isCompleted, setIsCompleted] = useState(false);
    const [tradeList, setTradeList] = useState([]);
    const [areaType, setAreaType] = useRecoilState(areaTypeState);
    const [baseDate, setBaseDate] = useRecoilState(tradeDateState);
    const [tradeDate, setTradeDate] = useState(baseDate);
    // const [region, setRegion] = useRecoilState(regionState);
    const [filterArea, setFilterArea] = useRecoilState(filterAreaState);
    const region = useRecoilValue(regionState);
    const [count, setCount] = useState(0);
    const [tradeType, setTradeType] = useRecoilState(tradeTypeState);

    useEffect(() => {
        if (tradeList.length === 0) {
            fetch();
        }
    }, [tradeList]);

    useEffect(() => {
        setTradeDate(baseDate);
    }, [baseDate]);

    useEffect(() => {
        setPage(0);
        setTradeList([]);
        setIsLoading(false);
        setIsCompleted(false);
        // fetch();
    }, [region, tradeType, areaType, filterArea, tradeDate]);

    const fetch = async () => {
        if (!isCompleted && !isLoading) {
            setIsLoading(true);
            let date = '';
            if (tradeDate !== null) {
                date = tradeDate.format('YYYYMM');
            }
            let startArea = getStartArea(filterArea[0]);
            let endArea = getEndArea(filterArea[1]);

            let result = [];
            if (region.type === 'BUILDING') {
                result = await getTrade(tradeType, region.buildingId, page, areaType.areaId, date);
            } else {
                result = await getRegionTrade(tradeType, region.id, page, startArea, endArea, date);
            }

            if (result.length === 0) {
                setIsCompleted(true);
            }

            let data = tradeList.concat(result.map((trade, idx) => getRow(page, idx, trade)));
            setTradeList(data);
            setPage(page + 1);
            setIsLoading(false);
        }
    };

    const getRow = (page, index, trade) => {
        if (region.type === 'BUILDING') {
            return <GuavaTradeRow page={page} idx={index} trade={trade}/>;
        } else {
            return <GuavaRegionTradeRow page={page} idx={index} trade={trade}/>;
        }
        return <></>;
    };

    const getCol = () => {
        if (region.type === 'BUILDING') {
            return <GuavaTradeCol/>;
        } else {
            return <GuavaRegionTradeCol/>;
        }
    };

    const table = () => {
        return (
            <>
                <div className={cx('body')}>
                    <InfiniteScroll
                        dataLength={tradeList.length} //This is important field to render the next data
                        next={fetch}
                        hasMore={!isCompleted}
                        loader={<GuavaLoading isLoading={isLoading}/>}
                        endMessage={tradeList.length > 0 &&
                        <div className={cx('empty_result')}>총 검색결과 {tradeList.length}개</div>}
                        // below props only if you need pull down functionality
                        // // refreshFunction={fetch}
                        // // pullDownToRefresh
                        // // pullDownToRefreshThreshold={50}
                        // // pullDownToRefreshContent={
                        // //     <h3 style={{textAlign: 'center'}}>&#8595; Pull down to refresh</h3>
                        // // }
                        // releaseToRefreshContent={
                        //     <h3 style={{textAlign: 'center'}}>&#8593; Release to refresh</h3>
                        // }
                    >
                        {tradeList}
                    </InfiniteScroll>
                </div>
            </>
        )
    };

    const empty = () => {
        return (
            tradeList.length === 0 && isCompleted &&
            <div className={cx('empty_result')}>검색결과가 없습니다</div>
        )
    };

    return (
        <>
            {
                tradeDate &&
                <div className={cx('date')}>
                    <div className={cx('back_btn')} onClick={() => {
                        setTradeDate(null);
                        setCount(0);
                        // fetchChart();
                    }}>
                        <ArrowLeftOutlined/>
                    </div>
                    <span>{tradeDate.format('YYYY년 M월')}</span>
                    <span className={cx('count')}>({tradeList.length}건)</span>
                </div>
            }
            <div className={cx('list')}>
                {getCol()}
                {table()}
                {empty()}
            </div>
        </>
    );
};

export default GuavaTable;